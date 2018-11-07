// --------------------------------------------------------------------------------
// Copyright 2002-2018 Echo Three, LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// --------------------------------------------------------------------------------

package com.echothree.ui.cli.mailtransfer.blogentry;

import com.echothree.control.user.authentication.common.AuthenticationUtil;
import com.echothree.control.user.authentication.common.AuthenticationService;
import com.echothree.control.user.forum.common.ForumUtil;
import com.echothree.control.user.forum.common.edit.BlogEntryEdit;
import com.echothree.control.user.forum.common.form.CreateBlogEntryForm;
import com.echothree.control.user.forum.common.form.CreateForumForumThreadForm;
import com.echothree.control.user.forum.common.form.CreateForumMessageAttachmentDescriptionForm;
import com.echothree.control.user.forum.common.form.CreateForumMessageAttachmentForm;
import com.echothree.control.user.forum.common.form.EditBlogEntryForm;
import com.echothree.control.user.forum.common.result.CreateBlogEntryResult;
import com.echothree.control.user.forum.common.result.EditBlogEntryResult;
import com.echothree.control.user.forum.common.spec.ForumMessageSpec;
import com.echothree.model.control.core.common.MimeTypes;
import com.echothree.model.control.party.common.PartyConstants;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.ui.cli.mailtransfer.blogentry.BlogEntryTransfer.CollectedParts.CapturedMessageAttachment;
import com.echothree.util.common.cyberneko.HtmlWriter;
import com.echothree.util.common.string.StringUtils;
import com.echothree.util.common.string.XmlUtils;
import com.echothree.util.common.command.CommandResult;
import com.echothree.util.common.command.EditMode;
import com.echothree.util.common.command.ExecutionResult;
import com.echothree.util.common.persistence.type.ByteArray;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.html.HtmlEscapers;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.pop3.POP3;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.pop3.POP3SClient;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

public class BlogEntryTransfer {
    
    private final boolean doVerbose;
    private final Configuration configuration;

    /** Creates a new instance of BlogEntryTransfer */
    public BlogEntryTransfer(Configuration configuration, boolean doVerbose) {
        this.doVerbose = doVerbose;
        this.configuration = configuration;
    }

    private final Log log = LogFactory.getLog(this.getClass());
    private UserVisitPK userVisitPK = null;
    
    private AuthenticationService getAuthenticationService()
            throws NamingException {
        return AuthenticationUtil.getHome();
    }
    
    private UserVisitPK getUserVisit() {
        if(userVisitPK == null) {
            try {
                userVisitPK = getAuthenticationService().getDataLoaderUserVisit();
            } catch (NamingException ne) {
                System.err.println("Unable to locate authentication service");
            }
        }
        
        return userVisitPK;
    }
    
    private void clearUserVisit() {
        if(userVisitPK != null) {
            try {
                getAuthenticationService().invalidateUserVisit(userVisitPK);
                userVisitPK = null;
            } catch (NamingException ne) {
                System.err.println("Unable to locate authentication service");
            }
            
            userVisitPK = null;
        }
    }


    String rewriteImageUrls(String cmsServlet, String html, String forumMessageName, Map<String, CapturedMessageAttachment> capturedMessageAttachmentsByCid)
            throws IOException {
        XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), null);
        XMLParserConfiguration parser = new HTMLConfiguration();

        ImageSourceTransformFilter imageSourceTransformFilter = new ImageSourceTransformFilter(cmsServlet, forumMessageName, capturedMessageAttachmentsByCid);

        // write to file with specified encoding
        OutputStream stream = new ByteArrayOutputStream();
        String encoding = "utf-8";
        XMLDocumentFilter writer = new HtmlWriter(stream, encoding);

        XMLDocumentFilter[] filters = { imageSourceTransformFilter, writer };

        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        parser.setProperty("http://cyberneko.org/html/properties/balance-tags/fragment-context-stack",
                XmlUtils.getInstance().toQNames(new String[] { "html", "body" }));

        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);

        parser.parse(source);

        return stream.toString();
    }

    String getBodyContents(String html)
            throws IOException {
        XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), null);
        XMLParserConfiguration parser = new HTMLConfiguration();

        BodyContentsOnlyFilter bodyContentsOnlyFilter = new BodyContentsOnlyFilter();
        //ImageSourceTransformFilter imageSourceTransformFilter = new ImageSourceTransformFilter();

        // write to file with specified encoding
        OutputStream stream = new ByteArrayOutputStream();
        String encoding = "utf-8";
        XMLDocumentFilter writer = new HtmlWriter(stream, encoding);

        XMLDocumentFilter[] filters = { bodyContentsOnlyFilter, writer };

        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        parser.setProperty("http://cyberneko.org/html/properties/balance-tags/fragment-context-stack",
                XmlUtils.getInstance().toQNames(new String[] { "html", "body" }));

        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);

        parser.parse(source);

        return stream.toString();
    }

    String getCmsTranformed(String cmsBaseUrl, String html)
            throws IOException {
        XMLInputSource source = new XMLInputSource(null, null, null, new StringReader(html), null);
        XMLParserConfiguration parser = new HTMLConfiguration();

        CmsTransformFilter cmsTransformFilter = new CmsTransformFilter(cmsBaseUrl);

        // write to file with specified encoding
        OutputStream stream = new ByteArrayOutputStream();
        String encoding = "utf-8";
        XMLDocumentFilter writer = new HtmlWriter(stream, encoding);

        XMLDocumentFilter[] filters = { cmsTransformFilter, writer };

        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        parser.setFeature("http://xml.org/sax/features/namespaces", false);

        parser.parse(source);

        return stream.toString();
    }

    class CollectedParts {

        boolean nativeFancyContent = false;
        int depthToCapture = 0;
        List<CapturedMessagePart> capturedMessageParts = new ArrayList<>();
        List<CapturedMessageAttachment> capturedMessageAttachments = new ArrayList<>();
        int forumMessageAttachmentSequence = 0;
        Map<String, CapturedMessageAttachment> capturedMessageAttachmentsByCid = new HashMap<>();

        CollectedParts() {
            log.info("new CollectedParts");
        }

        void addCapturedMessagePart(String html) {
            capturedMessageParts.add(new CapturedMessagePart(html));
        }

        class CapturedMessagePart {
            String html;

            CapturedMessagePart(String html) {
                this.html = html;
            }
        }

        String getCapturedHtml() {
            StringBuilder capturedHtml = new StringBuilder();

            capturedMessageParts.stream().forEach((capturedMessagePart) -> {
                capturedHtml.append(capturedMessagePart.html);
            });

            return capturedHtml.toString();
        }

        String addCapturedMessagePart(String cid, MimeType mimeType, ByteArray blob, String description) {
            CapturedMessageAttachment capturedMessageAttachment = new CapturedMessageAttachment(forumMessageAttachmentSequence++, mimeType, blob, description);
            String returnedCid = null;

            capturedMessageAttachments.add(capturedMessageAttachment);
            if(cid == null) {
                returnedCid = "Generated-" + forumMessageAttachmentSequence;
            }
            capturedMessageAttachmentsByCid.put(cid == null ? new StringBuilder("<").append(returnedCid).append(">").toString() : cid, capturedMessageAttachment);

            return returnedCid;
        }

        class CapturedMessageAttachment {
            Integer forumMessageAttachmentSequence;
            MimeType mimeType;
            ByteArray blob;
            String description;

            CapturedMessageAttachment(Integer forumMessageAttachmentSequence, MimeType mimeType, ByteArray blob, String description) {
                this.forumMessageAttachmentSequence = forumMessageAttachmentSequence;
                this.mimeType = mimeType;
                this.blob = blob;
                this.description = description;
            }
        }

    }

    String wrapInHtmlDocument(String html) {
        return new StringBuilder("<html><body>").append(html).append("</body></html>").toString();
    }

    String commandResultToHtmlDocument(CommandResult commandResult) {
        return wrapInHtmlDocument(HtmlEscapers.htmlEscaper().escape(commandResult.toString()));
    }

    String createBlogEntry(String cmsServlet, String username, List<String> forumNames, String title, CollectedParts collectedParts)
            throws NamingException, IOException {
        CreateBlogEntryForm createBlogEntryForm = ForumUtil.getHome().getCreateBlogEntryForm();
        String finalContent;

        createBlogEntryForm.setUsername(username);
        createBlogEntryForm.setForumName(forumNames.get(0));
        createBlogEntryForm.setLanguageIsoName(PartyConstants.Language_en);
        createBlogEntryForm.setSortOrder("1");
        createBlogEntryForm.setTitle(title);
        createBlogEntryForm.setContentMimeTypeName(MimeTypes.TEXT_HTML.mimeTypeName());
        createBlogEntryForm.setContent("placeholder");

        CommandResult commandResult = ForumUtil.getHome().createBlogEntry(getUserVisit(), createBlogEntryForm);

        if(!commandResult.hasErrors()) {
            ExecutionResult executionResult = commandResult.getExecutionResult();
            CreateBlogEntryResult result = (CreateBlogEntryResult)executionResult.getResult();
            String forumThreadName = result.getForumThreadName();
            String forumMessageName = result.getForumMessageName();
            String html = rewriteImageUrls(cmsServlet, collectedParts.getCapturedHtml(), forumMessageName, collectedParts.capturedMessageAttachmentsByCid);
            
            log.info("Captured HTML: " + html);
            finalContent = wrapInHtmlDocument(html);
            
            for(CapturedMessageAttachment capturedMessageAttachment : collectedParts.capturedMessageAttachments) {
                CreateForumMessageAttachmentForm createForumMessageAttachmentForm = ForumUtil.getHome().getCreateForumMessageAttachmentForm();
                MimeType mimeType = capturedMessageAttachment.mimeType;
                String mimeTypeName = mimeType.getPrimaryType() + "/" + mimeType.getSubType();
                String description = capturedMessageAttachment.description;

                if(mimeTypeName.equals("image/jpg")) {
                    mimeTypeName = MimeTypes.IMAGE_JPEG.mimeTypeName();
                }

                createForumMessageAttachmentForm.setForumMessageName(forumMessageName);
                createForumMessageAttachmentForm.setForumMessageAttachmentSequence(capturedMessageAttachment.forumMessageAttachmentSequence.toString());
                createForumMessageAttachmentForm.setMimeTypeName(mimeTypeName);
                createForumMessageAttachmentForm.setBlob(capturedMessageAttachment.blob);

                commandResult = ForumUtil.getHome().createForumMessageAttachment(getUserVisit(), createForumMessageAttachmentForm);

                if(commandResult.hasErrors()) {
                    log.error(commandResult);
                } else if(description != null) {
                    CreateForumMessageAttachmentDescriptionForm createForumMessageAttachmentDescriptionForm = ForumUtil.getHome().getCreateForumMessageAttachmentDescriptionForm();

                    createForumMessageAttachmentDescriptionForm.setForumMessageName(forumMessageName);
                    createForumMessageAttachmentDescriptionForm.setForumMessageAttachmentSequence(capturedMessageAttachment.forumMessageAttachmentSequence.toString());
                    createForumMessageAttachmentDescriptionForm.setLanguageIsoName(PartyConstants.Language_en);
                    createForumMessageAttachmentDescriptionForm.setDescription(description);

                    commandResult = ForumUtil.getHome().createForumMessageAttachmentDescription(getUserVisit(), createForumMessageAttachmentDescriptionForm);

                    if(commandResult.hasErrors()) {
                        log.error(commandResult);

                        finalContent = commandResultToHtmlDocument(commandResult);
                    }
                }
            }

            {
                EditBlogEntryForm commandForm = ForumUtil.getHome().getEditBlogEntryForm();
                ForumMessageSpec spec = ForumUtil.getHome().getForumMessageSpec();

                commandForm.setSpec(spec);
                spec.setForumMessageName(forumMessageName);

                commandForm.setEditMode(EditMode.LOCK);

                commandResult = ForumUtil.getHome().editBlogEntry(getUserVisit(), commandForm);
                executionResult = commandResult.getExecutionResult();
                EditBlogEntryResult editBlogEntryResult = (EditBlogEntryResult)executionResult.getResult();

                if(editBlogEntryResult != null) {
                    BlogEntryEdit edit = editBlogEntryResult.getEdit();

                    edit.setContent(html);

                    commandForm.setEdit(edit);
                    commandForm.setEditMode(EditMode.UPDATE);

                    commandResult = ForumUtil.getHome().editBlogEntry(getUserVisit(), commandForm);

                    if(commandResult.hasErrors()) {
                        log.error(commandResult);

                        finalContent = commandResultToHtmlDocument(commandResult);
                    }
                }
            }

            {
                for(int i = 1 ; i < forumNames.size() ; i++) {
                    CreateForumForumThreadForm commandForm = ForumUtil.getHome().getCreateForumForumThreadForm();

                    commandForm.setForumName(forumNames.get(i));
                    commandForm.setForumThreadName(forumThreadName);
                    commandForm.setIsDefault(Boolean.FALSE.toString());
                    commandForm.setSortOrder("1");

                    commandResult = ForumUtil.getHome().createForumForumThread(getUserVisit(), commandForm);

                    if(commandResult.hasErrors()) {
                        log.error(commandResult);

                        finalContent = commandResultToHtmlDocument(commandResult);
                    }
                }
            }
        } else {
            log.error(commandResult);

            finalContent = commandResultToHtmlDocument(commandResult);
        }

        return finalContent;
    }


    String captureContent(String cmsServlet, String username, List<String> forumNames, String title, Object content)
            throws MessagingException, MimeTypeParseException, IOException, NamingException {
        CollectedParts collectedParts = captureContent(2, null, content);
        String finalContent = null;

        if(collectedParts != null) {
            finalContent = createBlogEntry(cmsServlet, username, forumNames, title, collectedParts);
        } else {
            log.error("collectedParts are null");
        }

        return finalContent;
    }

    CollectedParts captureContent(int depth, CollectedParts collectedParts, Object content)
            throws MessagingException, MimeTypeParseException, IOException {
        String indent = StringUtils.getInstance().repeatingStringFromChar(' ', depth);

        if(content instanceof String) {
            String html =  StringUtils.getInstance().convertToHtml((String)content, MimeTypes.TEXT_X_MARKUP.mimeTypeName());

            if(collectedParts == null) {
                collectedParts = new CollectedParts();
            }
            collectedParts.addCapturedMessagePart(html);

            log.info(indent + "text/plain");
        } else if(content instanceof MimeMultipart) {
            MimeMultipart mimeMultipartContent = (MimeMultipart)content;
            MimeType mimeType = new MimeType(mimeMultipartContent.getContentType());
            String subType = mimeType.getSubType();
            String mimeTypeName = mimeType.getPrimaryType() + "/" + subType;

            log.info(indent + "MimeMultipart's mimeTypeName: " + mimeTypeName);

            switch (subType) {
                case "mixed":
                case "related":
                    collectedParts = new CollectedParts();
                    
                    for (int x = 0; x < mimeMultipartContent.getCount(); x++) {
                        BodyPart bodyPart = mimeMultipartContent.getBodyPart(x);
                        
                        captureContent(depth + 2, collectedParts, bodyPart);
                    }   break;
                case "alternative":
                    int count = mimeMultipartContent.getCount();
                    List<CollectedParts> alternativeCollectedParts = new ArrayList<>(count);
                    
                    for (int x = 0; x < mimeMultipartContent.getCount(); x++) {
                        CollectedParts altCollectedParts = new CollectedParts();
                        BodyPart bodyPart = mimeMultipartContent.getBodyPart(x);
                        
                        captureContent(depth + 2, altCollectedParts, bodyPart);
                        
                        alternativeCollectedParts.add(altCollectedParts);
                        log.info(indent + "nativeFancyContent: " + altCollectedParts.nativeFancyContent);
                    }
                    
                    log.info(indent + "alternatives count: " + alternativeCollectedParts.size());

                    for(CollectedParts alternativeCollectedPart : alternativeCollectedParts) {
                        if(alternativeCollectedPart.nativeFancyContent) {
                            collectedParts = alternativeCollectedPart;
                            log.info(indent + "found nativeFancyContent");
                        }
                    }
                    break;
                default:
                    log.error(indent + "unknown subType: " + subType);
                    break;
            }
        } else if(content instanceof MimeBodyPart) {
            MimeBodyPart mimeBodyPart = (MimeBodyPart)content;
            MimeType mimeType = new MimeType(mimeBodyPart.getContentType());

            String primaryType = mimeType.getPrimaryType();
            String mimeTypeName = primaryType + "/" + mimeType.getSubType();
            log.info(indent + "MimeBodyPart's mimeTypeName: " + mimeTypeName);

            if(primaryType.equals("multipart")) {
                MimeMultipart mimeMultipartContent = (MimeMultipart)mimeBodyPart.getContent();

                for (int x = 0; x < mimeMultipartContent.getCount(); x++) {
                    BodyPart bodyPart = mimeMultipartContent.getBodyPart(x);

                    captureContent(depth + 2, collectedParts, bodyPart);
                }
            } else {
                String disposition = mimeBodyPart.getDisposition();
                String html = null;

                if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT) || disposition.equals(BodyPart.INLINE))) {
                    DataHandler handler = mimeBodyPart.getDataHandler();
                    String description = handler.getName();
                    String cid = mimeBodyPart.getContentID();

                    log.info(indent + "  description: " + handler.getName());
                    log.info(indent + "  disposition: " + disposition);
                    log.info(indent + "  cid: " + cid);

                    String returnedCid = collectedParts.addCapturedMessagePart(cid, mimeType, new ByteArray(IOUtils.toByteArray(handler.getInputStream())), description);
                    collectedParts.nativeFancyContent = true;

                    if(returnedCid != null) {
                        html = "<img src=\"cid:" + returnedCid + "\">";
                    }
                } else {
                    boolean alreadyHtml = mimeTypeName.equals("text/html");

                    if(mimeTypeName.equals(MimeTypes.TEXT_PLAIN.mimeTypeName())) {
                        mimeTypeName = MimeTypes.TEXT_X_MARKUP.mimeTypeName();
                    }

                    String mimeBodyPartContent = (String)mimeBodyPart.getContent();
                    html = alreadyHtml ? getBodyContents(mimeBodyPartContent) : StringUtils.getInstance().convertToHtml(mimeBodyPartContent, mimeTypeName);

                    collectedParts.nativeFancyContent |= alreadyHtml;
                }

                if(html != null) {
                    collectedParts.addCapturedMessagePart(html);
                }
            }
        } else {
            log.error(indent + "unknown: " + content.getClass().toString());
        }

        return collectedParts;
    }

    private String getTrimmedStringProperty(String property, boolean required) {
        String value = StringUtils.getInstance().trimToNull(configuration.getString(property));

        if(required && value == null) {
            log.error(property + " is a required property");
        }

        return value;
    }

    public void sendResponse(String cmsBaseUrl, String smtpServerName, String from, String to, String subject, String body) {
        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.put("mail.smtp.host", smtpServerName);

        // Get session
        Session session = Session.getDefaultInstance(props, null);

        // Define message
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            // message.setContent(HtmlCompressor.compress(new String(responseBody, charset), charset), contentType + "; charset=" + charset);
            message.setContent(cmsBaseUrl != null && cmsBaseUrl.length() > 0 ? getCmsTranformed(cmsBaseUrl, body) : body, "text/html; charset=UTF-8");

            // Send message
            Transport.send(message);
        } catch(IOException | MessagingException e) {
            log.error(e);
        }
    }

    private List<Address> getRecipientAddresses(MimeMessage mimeMessage)
            throws MessagingException {
        Address[] toAddresses = mimeMessage.getRecipients(RecipientType.TO);
        Address[] ccAddresses = mimeMessage.getRecipients(RecipientType.CC);
        List<Address> recipientAddresses = new ArrayList<>((toAddresses == null ? 0 : toAddresses.length) + (ccAddresses == null ? 0 : ccAddresses.length));

        if(toAddresses != null) {
            recipientAddresses.addAll(Arrays.asList(toAddresses));
        }

        if(ccAddresses != null) {
            recipientAddresses.addAll(Arrays.asList(ccAddresses));
        }

        return recipientAddresses;
    }

    public void transfer()
        throws Exception {
        boolean useTls = configuration.getBoolean("com.echothree.ui.cli.mailtransfer.blogentry.useTls", true);
        String pop3ServerName = getTrimmedStringProperty("com.echothree.ui.cli.mailtransfer.blogentry.serverName", true);
        String pop3Username = getTrimmedStringProperty("com.echothree.ui.cli.mailtransfer.blogentry.username", true);
        String pop3Password = getTrimmedStringProperty("com.echothree.ui.cli.mailtransfer.blogentry.password", true);
        String responseFrom = configuration.getString("com.echothree.ui.cli.mailtransfer.blogentry.responseFrom");
        String cmsBaseUrl = configuration.getString("com.echothree.ui.cli.mailtransfer.blogentry.cmsBaseUrl");
        String cmsServlet = configuration.getString("com.echothree.ui.cli.mailtransfer.blogentry.cmsServlet");
        String domainName = getTrimmedStringProperty("com.echothree.ui.cli.mailtransfer.blogentry.domainName", true);
        boolean trimDomainName = configuration.getBoolean("com.echothree.ui.cli.mailtransfer.blogentry.trimDomainName", false);

        if(pop3ServerName != null && pop3Username != null && pop3Password != null && domainName != null) {
            POP3SClient pop3sClient = null;

            try {
                pop3sClient = new POP3SClient(useTls);
                pop3sClient.connect(pop3ServerName);
                
                if(pop3sClient.getState() == POP3.AUTHORIZATION_STATE) {
                    pop3sClient.login(pop3Username, pop3Password);

                    if(pop3sClient.getState() == POP3.TRANSACTION_STATE) {
                        POP3MessageInfo[] pop3MessageInfos = pop3sClient.listMessages();

                        log.info("message count: " + pop3MessageInfos.length);

                        if(pop3MessageInfos.length > 0) {
                            int successfulMessages = 0;

                            for(int i = 0; i < pop3MessageInfos.length && successfulMessages < 10; i++) {
                                POP3MessageInfo pop3MessageInfo = pop3MessageInfos[i];
                                int messageId = pop3MessageInfo.number;
                                Reader reader = pop3sClient.retrieveMessage(messageId);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                StringBuilder stringBuilder = new StringBuilder();

                                log.info("message " + pop3MessageInfo.number + ", size = " + pop3MessageInfo.size);

                                for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                                    stringBuilder.append(line);
                                    stringBuilder.append('\n');
                                }

                                ByteArray message = new ByteArray(stringBuilder.toString().getBytes(Charsets.UTF_8));

                                String description;
                                String sender = null;
                                String username = null;
                                String sentDate = null;

                                try {
                                    Properties props = System.getProperties();
                                    javax.mail.Session mailSession = javax.mail.Session.getInstance(props);
                                    ByteArrayInputStream bais = new ByteArrayInputStream(message.byteArrayValue());
                                    MimeMessage mimeMessage = new MimeMessage(mailSession, bais);
                                    String finalContent;

                                    description = mimeMessage.getSubject();

                                    if(description != null) {
                                        int length = description.length();

                                        if(length > 80) {
                                            description = description.substring(0, 80);
                                        } else if(length == 0) {
                                            description = null;
                                        }
                                    }

                                    Address[] fromAddresses = mimeMessage.getFrom();
                                    if(fromAddresses != null && fromAddresses.length > 0) {
                                        Address address = fromAddresses[0];

                                        if(address instanceof InternetAddress) {
                                            sender = ((InternetAddress)address).getAddress();
                                        }
                                    }

                                    if(sender != null) {
                                        if(trimDomainName) {
                                            String[] senderSplit = Splitter.on('@').trimResults().omitEmptyStrings().splitToList(sender).toArray(new String[0]);

                                            if(senderSplit.length == 2) {
                                                String senderDomainName = senderSplit[1];

                                                if(senderDomainName.equals(domainName)) {
                                                    username = senderSplit[0];
                                                } else {
                                                    log.error("Sender domain name does not match configured domain name: " + sender);
                                                }
                                            } else {
                                                log.error("Couldn't split sender: " + sender);
                                            }
                                        } else {
                                            username = sender;
                                        }
                                    }

                                    if(username == null) {
                                        log.error("A valid username for posting wasn't found: " + sender);
                                    } else {
                                        List<Address> recipientAddresses = getRecipientAddresses(mimeMessage);
                                        List<String> forumNames = new ArrayList<>(recipientAddresses.size());
                                        
                                        Date date = mimeMessage.getSentDate();
                                        if (date != null) {
                                            sentDate = date.toString();
                                        }

                                        log.info("Subject: " + description);
                                        log.info("From: " + sender);
                                        log.info("Date: " + sentDate);
                                        
                                        recipientAddresses.stream().filter((recipientAddress) -> (recipientAddress instanceof InternetAddress)).map((recipientAddress) -> ((InternetAddress)recipientAddress).getAddress()).forEach((recipient) -> {
                                            String[] recipientSplit = Splitter.on('@').trimResults().omitEmptyStrings().splitToList(recipient).toArray(new String[0]);
                                            
                                            if(recipientSplit.length == 2) {
                                                String foundDomainName = recipientSplit[1];
                                                
                                                if(foundDomainName.equals(domainName)) {
                                                    String foundForumName = recipientSplit[0];
                                                    String configurationKey = "com.echothree.ui.cli.mailtransfer.blogentry.forumName." + foundForumName;
                                                    String configurationValue = configuration.getString(configurationKey);
                                                    String forumName = configurationValue == null ? foundForumName : configurationValue;
                                                    
                                                    forumNames.add(forumName);
                                                    log.info("To: " + recipient + ", posting to: " + forumName);
                                                } else {
                                                    log.error("Ignoring recipient due to domain name: " + recipient);
                                                }
                                            } else {
                                                log.error("Couldn't split recipient: " + recipient);
                                            }
                                        });

                                        if(forumNames.size() > 0) {
                                            finalContent = captureContent(cmsServlet, username, forumNames, description, mimeMessage.getContent());

                                            if(responseFrom != null && responseFrom.length() > 0) {
                                                sendResponse(cmsBaseUrl, pop3ServerName, responseFrom, sender, "Your Posting: " + description, finalContent);
                                            }
                                        } else {
                                            log.error("Ignoring message, no valid forum names found.");
                                        }
                                    }

                                    pop3sClient.deleteMessage(messageId);
                                } catch(MessagingException me) {
                                    log.error("MessagingException");
                                } catch(MimeTypeParseException mtpe) {
                                    log.error("MimeTypeParseException");
                                }
                            }
                        }

                        pop3sClient.logout();
                    } else {
                        log.error("login to " + pop3Username + "@" + pop3ServerName + " failed");
                    }
                } else {
                    log.error("connection to " + pop3ServerName + " failed");
                }
            } catch(NamingException ne) {
                throw ne;
            } catch(SocketException se) {
                throw se;
            } catch(IOException ioe) {
                throw ioe;
            } finally {
                if(pop3sClient != null) {
                    if(pop3sClient.getState() == POP3.TRANSACTION_STATE) {
                        pop3sClient.logout();
                    }

                    pop3sClient.disconnect();
                }
            }

            clearUserVisit();
        }
    }
    
}
