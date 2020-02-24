// --------------------------------------------------------------------------------
// Copyright 2002-2020 Echo Three, LLC
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

package com.echothree.util.client.string;

import com.echothree.control.user.party.client.helper.NameSuffixesHelper;
import com.echothree.control.user.party.client.helper.PersonalTitlesHelper;
import com.echothree.model.data.user.common.pk.UserVisitPK;
import com.echothree.util.common.string.NameResult;
import com.echothree.util.common.string.StringUtils;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;

public final class NameCleaner {

    StringUtils stringUtils = StringUtils.getInstance();

    /** Creates a new instance of NameCleaner */
    public NameCleaner(UserVisitPK userVisitPK)
            throws NamingException {
        loadPersonalTitles(userVisitPK);
        loadNameSuffixes(userVisitPK);
    }

    protected Map<String, String> personalTitles;
    protected Map<String, String> personalTitlesOriginal;
    protected int maxPersonalTitleSpaces = 0;

    protected String cleanStringForTitleOrSuffix(String str) {
        return stringUtils.cleanString(str, true, true, true).toLowerCase();
    }

    protected final void loadPersonalTitles(UserVisitPK userVisitPK)
            throws NamingException {
        var personalTitleChoices = PersonalTitlesHelper.getInstance().getPersonalTitleChoices(userVisitPK, Boolean.FALSE);
        var valueIter = personalTitleChoices.getValues().iterator();
        var labelIter = personalTitleChoices.getLabels().iterator();

        personalTitles = new HashMap<>(personalTitleChoices.getLabels().size());
        personalTitlesOriginal = new HashMap<>(personalTitleChoices.getLabels().size());
        while(valueIter.hasNext()) {
            var originalLabel = labelIter.next();
            var label = cleanStringForTitleOrSuffix(originalLabel);
            var value = valueIter.next();
            var spaceCount = stringUtils.countSpaces(label);

            personalTitles.put(label, value);
            personalTitlesOriginal.put(value, originalLabel);

            if(spaceCount > maxPersonalTitleSpaces) {
                maxPersonalTitleSpaces = spaceCount;
            }
        }
    }

    public Map<String, String> getPersonalTitles() {
        return personalTitles;
    }

    public Map<String, String> getPersonalTitlesOriginal() {
        return personalTitlesOriginal;
    }

    protected Map<String, String> nameSuffixes;
    protected Map<String, String> nameSuffixesOriginal;
    protected int maxNameSuffixSpaces = 0;

    protected void loadNameSuffixes(UserVisitPK userVisitPK)
            throws NamingException {
        var nameSuffixChoices = NameSuffixesHelper.getInstance().getNameSuffixChoices(userVisitPK, Boolean.FALSE);
        var valueIter = nameSuffixChoices.getValues().iterator();
        var labelIter = nameSuffixChoices.getLabels().iterator();

        nameSuffixes = new HashMap<>(nameSuffixChoices.getLabels().size());
        nameSuffixesOriginal = new HashMap<>(nameSuffixChoices.getLabels().size());
        while(valueIter.hasNext()) {
            var originalLabel = labelIter.next();
            var label = cleanStringForTitleOrSuffix(originalLabel);
            var value = valueIter.next();
            var spaceCount = stringUtils.countSpaces(label);

            nameSuffixes.put(label, value);
            nameSuffixesOriginal.put(value, originalLabel);

            if(spaceCount > maxNameSuffixSpaces) {
                maxNameSuffixSpaces = spaceCount;
            }
        }
    }

    public Map<String, String> getNameSuffixes() {
        return nameSuffixes;
    }

    public Map<String, String> getNameSuffixesOriginal() {
        return nameSuffixesOriginal;
    }

    protected List<String> iterableToList(Iterable<String> pieces) {
        var list = new ArrayList<String>();

        for(String str : pieces) {
            list.add(str);
        }

        return list;
    }

    protected static final Splitter SpaceSplitter = Splitter.on(' ')
           .trimResults()
           .omitEmptyStrings();

    protected static int MaximumFirstNameLength = 20;
    protected static int MaximumMiddleNameLength = 20;
    protected static int MaximumLastNameLength = 20;

    public NameResult getCleansedName(final String str) {
        String personalTitleChoice = null;
        int personalTitlePieces;
        String firstName = null;
        String middleName = null;
        String lastName = null;
        String nameSuffixChoice = null;
        int nameSuffixPieces;

        // 1) Break apart str into a List at any space character.
        var pieces = iterableToList(SpaceSplitter.split(str));
        var piecesSize = pieces.size();
        var startingIndex = 0;
        var endingIndex = piecesSize - 1;

        // 2) Find the longest (in words) personal title.
        for(var i = Math.min(maxPersonalTitleSpaces, endingIndex - startingIndex) ; i >= startingIndex ; i--) {
            var personalTitle = new StringBuilder();

            for(int j = 0 ; j <= i ; j++) {
                if(j > 0) {
                    personalTitle.append(' ');
                }

                personalTitle.append(pieces.get(startingIndex + j));
            }

            personalTitleChoice = personalTitles.get(cleanStringForTitleOrSuffix(personalTitle.toString()));

            if(personalTitleChoice != null) {
                personalTitlePieces = i - startingIndex + 1;
                startingIndex += personalTitlePieces;
                break;
            }
        }

        // 3) Find the longest (in words) name suffix.
        if(startingIndex <= endingIndex) {
            for(var i = Math.min(endingIndex == 0 ? 0 : endingIndex - maxNameSuffixSpaces, endingIndex - startingIndex) ; i <= endingIndex ; i++) {
                var nameSuffix = new StringBuilder();

                for(int j = i ; j <= endingIndex ; j++) {
                    if(j > i) {
                        nameSuffix.append(' ');
                    }

                    nameSuffix.append(pieces.get(j));
                }

                nameSuffixChoice = nameSuffixes.get(cleanStringForTitleOrSuffix(nameSuffix.toString()));

                if(nameSuffixChoice != null) {
                    nameSuffixPieces = endingIndex - i + 1;
                    endingIndex -= nameSuffixPieces;
                    break;
                }
            }
        }

        // 4) Deal with commas and flipped names ("Harms, Richard").
        if(startingIndex <= endingIndex) {
            var firstPiece = pieces.get(startingIndex);

            if(firstPiece.endsWith(",") && firstPiece.length() > 1) {
                // If the first piece ends with a comma, assume they put their last name first, and flip things around.
                for(var i = startingIndex + 1 ; i <= endingIndex ; i++) {
                    pieces.set(i - 1, pieces.get(i));
                }

                pieces.set(endingIndex, firstPiece);
            }

            // Trim all trailing commas, and remove any new empty elements.
            for(var i = startingIndex ; i <= endingIndex ; i++) {
                var element = pieces.get(i);

                if(element.endsWith(",")) {
                    element = element.substring(0, element.length() - 1);

                    if(element.length() == 0) {
                        pieces.remove(i);
                        endingIndex--;

                        if(pieces.isEmpty()) {
                            break;
                        } else {
                            i--;
                        }
                    } else {
                        pieces.set(i, element);
                    }
                }
            }
        }

        // 5) Pick out bits of a name.
        if(startingIndex <= endingIndex) {
            var done = false;

            if(personalTitleChoice != null && startingIndex - endingIndex == 0) {
                // Mr. Harms
                lastName = pieces.get(startingIndex);
                done = true;
            } else {
                if(nameSuffixChoice != null && startingIndex - endingIndex == 0) {
                    // Richard Jr.
                    firstName = pieces.get(startingIndex);
                    done = true;
                } else {
                    // Richard
                    if(personalTitleChoice == null && nameSuffixChoice == null && piecesSize == 1) {
                        firstName = pieces.get(startingIndex);
                        done = true;
                    }
                }
            }

            if(!done) {
                firstName = pieces.get(startingIndex);
                startingIndex++;

                if(startingIndex == endingIndex) {
                    lastName = pieces.get(startingIndex);
                } else {
                    StringBuilder sb = new StringBuilder();

                    for(int i = startingIndex ; i < endingIndex ; i++) {
                        String toAppend = pieces.get(i);

                        if(i != startingIndex) {
                            sb.append(' ');
                        }

                        sb.append(stringUtils.isAllSameCase(toAppend) ? stringUtils.normalizeCase(toAppend) : toAppend);
                    }

                    middleName = sb.toString();
                    lastName = pieces.get(endingIndex);
                }
            }
        }

        // X) If any pieces of the name are all upper-case, or are all lower-case, fix them.
        if(stringUtils.isAllSameCase(firstName)) {
            firstName = stringUtils.normalizeCase(firstName);
        }

        if(stringUtils.isAllSameCase(lastName)) {
            lastName = stringUtils.normalizeCase(lastName);
        }

        return new NameResult(personalTitleChoice,
                stringUtils.left(firstName, MaximumFirstNameLength),
                stringUtils.left(middleName, MaximumMiddleNameLength),
                stringUtils.left(lastName, MaximumLastNameLength),
                nameSuffixChoice);
    }

}
