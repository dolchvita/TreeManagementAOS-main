package com.snd.app.domain.tree.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class TreeHashtagDTO {
    private String nfc;
    private String hashtag;

    public List<String> extractHashTags() {
        List<String> hashTags = new ArrayList<>();
        Pattern pattern = Pattern.compile("^[가-힣a-zA-Z0-9-]+$");
        Matcher matcher = pattern.matcher(this.hashtag);

        while (matcher.find()) {
            hashTags.add(matcher.group());
        }
        return hashTags;
        /**
         * 정규식 기준 한글/영문/하이픈 허용 공백X
         * 출력예시
         * 만약 hashtag String 이
         * #가나다라#보호수-1#은행나무#example
         * 이라고 가정할때
         * '#' 기준으로 문자열 분리
         * #가나다라
         * #보호수-1
         * #은행나무
         * #example
         *
         * 배열로 표현시
         * ["#가나다라", "#보호수-1", "#은행나무", "#example"]
         *
         *
         * // 정규식 검증 -
         *         String input = treeHashtag.getHashtag();
         *         String[] inputHashtagStrings = input.split("#");
         *
         *         for (String hashtagString : inputHashtagStrings) {
         *             if (!hashtagString.isEmpty()) {
         *                 Matcher matcher = HASHTAG_REGEX.matcher(hashtagString);
         *                 if (!matcher.matches()) {
         *                     return ResponseVO.builder()
         *                             .result("fail")
         *                             .message("Invalid Hashtag Format")
         *                             .build();
         *                 }
         *             }
         *         }
         *
         */
    }
}
