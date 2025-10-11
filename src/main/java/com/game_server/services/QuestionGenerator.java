/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.game_server.services;

import java.util.*;

/**
 *
 * @author ADMIN
 */
public class QuestionGenerator {
    
    private static final String[] LETTERS = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    
    private static final Random random = new Random();

    public static QuestionData generateQuestion() {
        // Random loại: số hoặc chữ (true = số, false = chữ)
        boolean isNumbers = random.nextBoolean();
        
        // Random thứ tự sắp xếp (true = tăng dần, false = giảm dần)
        boolean isAscending = random.nextBoolean();
        
        // Random số lượng items (5, 10, hoặc 15)
        int[] itemCounts = {5, 10, 15};
        int itemCount = itemCounts[random.nextInt(itemCounts.length)];
        
        // Tính thời gian dựa trên số lượng items
        int timeLimit = calculateTimeLimit(itemCount);
        
        // Tạo dãy items
        List<String> items = generateItems(isNumbers, itemCount);
        
        return new QuestionData(isNumbers, isAscending, items, timeLimit);
    }
    
    /**
     * Class để chứa thông tin câu hỏi
     */
    public static class QuestionData {
        private final boolean isNumbers;
        private final boolean isAscending;
        private final List<String> items;
        private final int timeLimit;
        
        public QuestionData(boolean isNumbers, boolean isAscending, List<String> items, int timeLimit) {
            this.isNumbers = isNumbers;
            this.isAscending = isAscending;
            this.items = items;
            this.timeLimit = timeLimit;
        }
        
        public boolean isNumbers() { return isNumbers; }
        public boolean isAscending() { return isAscending; }
        public List<String> getItems() { return items; }
        public int getTimeLimit() { return timeLimit; }
    }
    
    /**
     * Tính thời gian dựa trên số lượng items
     */
    private static int calculateTimeLimit(int itemCount) {
        return switch (itemCount) {
            case 5 -> 15;   // 5 items = 15 giây
            case 10 -> 30;  // 10 items = 30 giây
            case 15 -> 45;  // 15 items = 45 giây
            default -> 30;  // Default
        };
    }
    
    /**
     * Tạo danh sách items
     */
    private static List<String> generateItems(boolean isNumbers, int count) {
        List<String> items = new ArrayList<>();
        
        if (isNumbers) {
            // Tạo số ngẫu nhiên từ 1-100, không trùng lặp
            Set<Integer> numberSet = new HashSet<>();
            while (numberSet.size() < count) {
                numberSet.add(random.nextInt(100) + 1);
            }
            for (Integer num : numberSet) {
                items.add(String.valueOf(num));
            }
        } else {
            // Chọn chữ cái ngẫu nhiên từ bảng chữ cái tiếng Anh
            List<String> letterList = Arrays.asList(LETTERS);
            Collections.shuffle(letterList);
            for (int i = 0; i < Math.min(count, letterList.size()); i++) {
                items.add(letterList.get(i));
            }
        }
        
        // Shuffle để tạo thứ tự ngẫu nhiên
        Collections.shuffle(items);
        return items;
    }
}