package com.nau.lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class for implementing transposition ciphers
 */
public class TranspositionCipher {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Програма шифрування методом перестановки");
        System.out.println("Доступні методи шифрування:");
        System.out.println("1. Шифр простої одинарної перестановки");
        System.out.println("2. Шифр блокової одинарної перестановки");
        System.out.println("3. Шифр вертикальної перестановки з ключем");
        System.out.println("4. Шифр подвійної перестановки");
        System.out.println("5. Шифр решітки Кардано");

        System.out.print("Виберіть метод шифрування (1-5): ");
        int choice = Integer.parseInt(scanner.nextLine());

        System.out.print("Введіть ім'я вхідного файлу (або залиште порожнім для ручного вводу): ");
        String inputFile = scanner.nextLine();

        String text;
        if (inputFile.isEmpty()) {
            System.out.print("Введіть текст для шифрування: ");
            text = scanner.nextLine().toUpperCase();
        } else {
            text = readFromFile(inputFile).toUpperCase();
        }

        System.out.print("Введіть ім'я вихідного файлу (або залиште порожнім для виводу на екран): ");
        String outputFile = scanner.nextLine();

        String result;

        switch (choice) {
            case 1:
                result = singleTranspositionCipher(text);
                break;
            case 2:
                result = blockTranspositionCipher(text);
                break;
            case 3:
                result = columnTranspositionCipher(text);
                break;
            case 4:
                result = doubleTranspositionCipher(text);
                break;
            case 5:
                result = cardanoGrilleCipher(text);
                break;
            default:
                System.out.println("Невірний вибір!");
                return;
        }

        if (outputFile.isEmpty()) {
            System.out.println("Результат: " + result);
        } else {
            writeToFile(outputFile, result);
            System.out.println("Результат записано у файл: " + outputFile);
        }
    }

    /**
     * Simple Single Transposition Cipher
     */
    private static String singleTranspositionCipher(String text) {
        System.out.println("Шифр простої одинарної перестановки");
        System.out.print("Введіть послідовність перестановки (наприклад, 3,1,4,2,6,5 для рядка з 6 символів): ");
        String[] permutation = scanner.nextLine().split(",");

        int[] perm = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            perm[i] = Integer.parseInt(permutation[i]) - 1; // Convert to 0-based index
        }

        if (text.length() > perm.length) {
            System.out.println("Попередження: Довжина тексту більша за довжину перестановки. Буде використано тільки перші "
                               + perm.length + " символів.");
            text = text.substring(0, perm.length);
        } else if (text.length() < perm.length) {
            System.out.println("Попередження: Довжина тексту менша за довжину перестановки. Текст буде доповнено символами.");
            while (text.length() < perm.length) {
                text += "Ь"; // Padding with 'Ь'
            }
        }

        char[] result = new char[text.length()];
        for (int i = 0; i < perm.length; i++) {
            result[i] = text.charAt(perm[i]);
        }

        return new String(result);
    }

    /**
     * Block Transposition Cipher
     */
    private static String blockTranspositionCipher(String text) {
        System.out.println("Шифр блокової одинарної перестановки");
        System.out.print("Введіть розмір блоку: ");
        int blockSize = Integer.parseInt(scanner.nextLine());

        System.out.print("Введіть послідовність перестановки для блоку (наприклад, 3,1,2 для блоку з 3 символів): ");
        String[] permutation = scanner.nextLine().split(",");

        int[] perm = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            perm[i] = Integer.parseInt(permutation[i]) - 1; // Convert to 0-based index
        }

        if (blockSize != perm.length) {
            System.out.println("Помилка: Розмір блоку не відповідає довжині перестановки!");
            return text;
        }

        // Pad text to make its length a multiple of blockSize
        while (text.length() % blockSize != 0) {
            text += "Ь"; // Padding with 'Ь'
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += blockSize) {
            String block = text.substring(i, i + blockSize);
            char[] permutedBlock = new char[blockSize];

            for (int j = 0; j < blockSize; j++) {
                permutedBlock[j] = block.charAt(perm[j]);
            }

            result.append(permutedBlock);
        }

        return result.toString();
    }

    /**
     * Column Transposition Cipher with Key
     */
    private static String columnTranspositionCipher(String text) {
        System.out.println("Шифр вертикальної перестановки з ключем");
        System.out.print("Введіть ключове слово: ");
        String key = scanner.nextLine().toUpperCase();

        // Generate column order based on key
        int[] order = getColumnOrder(key);

        // Calculate number of rows needed
        int numColumns = key.length();
        int numRows = (int) Math.ceil((double) text.length() / numColumns);

        // Pad text if necessary
        while (text.length() < numRows * numColumns) {
            text += "Ь"; // Padding with 'Ь'
        }

        // Create the matrix
        char[][] matrix = new char[numRows][numColumns];
        int k = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                matrix[i][j] = text.charAt(k++);
            }
        }

        // Read out the columns according to the key order
        StringBuilder result = new StringBuilder();
        for (int col : order) {
            for (int row = 0; row < numRows; row++) {
                result.append(matrix[row][col]);
            }
        }

        return result.toString();
    }

    /**
     * Double Transposition Cipher
     */
    private static String doubleTranspositionCipher(String text) {
        System.out.println("Шифр подвійної перестановки");
        System.out.print("Введіть ключове слово для стовпців: ");
        String colKey = scanner.nextLine().toUpperCase();

        System.out.print("Введіть ключове слово для рядків: ");
        String rowKey = scanner.nextLine().toUpperCase();

        // Generate column and row orders based on keys
        int[] colOrder = getColumnOrder(colKey);
        int[] rowOrder = getColumnOrder(rowKey);

        int numColumns = colKey.length();
        int numRows = rowKey.length();

        // Pad text if necessary
        while (text.length() < numRows * numColumns) {
            text += "Ь"; // Padding with 'Ь'
        }

        // Create the initial matrix
        char[][] matrix = new char[numRows][numColumns];
        int k = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (k < text.length()) {
                    matrix[i][j] = text.charAt(k++);
                } else {
                    matrix[i][j] = 'Ь'; // Padding with 'Ь'
                }
            }
        }

        // Permute the columns
        char[][] colPermuted = new char[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                colPermuted[i][j] = matrix[i][colOrder[j]];
            }
        }

        // Permute the rows
        char[][] rowPermuted = new char[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                rowPermuted[i][j] = colPermuted[rowOrder[i]][j];
            }
        }

        // Read out the final matrix
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                result.append(rowPermuted[i][j]);
            }
        }

        return result.toString();
    }

    /**
     * Cardano Grille Cipher
     */

    private static String cardanoGrilleCipher(String text) {
        System.out.println("Шифр решітки Кардано");
        System.out.print("Введіть розмір решітки (N для квадрата NxN, N має бути парним): ");
        int size = Integer.parseInt(scanner.nextLine());

        if (size % 2 != 0) {
            System.out.println("Розмір решітки повинен бути парним числом!");
            return text;
        }

        // Number of holes in each quadrant
        int holesPerQuadrant = size * size / 4;

        // Create the grille - only first quadrant
        boolean[][] grille = new boolean[size][size];
        System.out.println("Введіть координати отворів у решітці (формат: рядок,стовпець)");
        System.out.println("Введіть " + holesPerQuadrant + " координат (по одній на рядок), натисніть Enter після кожної:");

        for (int i = 0; i < holesPerQuadrant; i++) {
            String[] coords = scanner.nextLine().split(",");
            int row = Integer.parseInt(coords[0]) - 1; // Convert to 0-based
            int col = Integer.parseInt(coords[1]) - 1; // Convert to 0-based

            if (row >= 0 && row < size / 2 && col >= 0 && col < size / 2) {
                grille[row][col] = true;
            } else {
                System.out.println("Неприпустимі координати! Використовуйте значення від 1 до " + (size / 2));
                i--; // Retry
            }
        }

        // Pad text if necessary
        while (text.length() < size * size) {
            text += "Ь"; // Padding with 'Ь'
        }

        // Use the grille to create the encrypted text
        char[][] matrix = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = 'Ь'; // Initialize with padding character
            }
        }

        int textIndex = 0;

        // Place characters through the grille with 4 rotations
        for (int rotation = 0; rotation < 4; rotation++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (grille[i][j]) {
                        if (textIndex < text.length()) {
                            matrix[i][j] = text.charAt(textIndex++);
                        } else {
                            matrix[i][j] = 'Ь'; // Use padding if text is too short
                        }
                    }
                }
            }

            // Rotate the grille 90 degrees clockwise
            rotateGrille(grille);
        }

        // Read out the matrix row by row
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result.append(matrix[i][j]);
            }
        }

        return result.toString();
    }

    /**
     * Rotate a grille 90 degrees clockwise
     */
    private static void rotateGrille(boolean[][] grille) {
        int size = grille.length;
        boolean[][] rotated = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = grille[i][j];
            }
        }

        // Copy back to original array
        for (int i = 0; i < size; i++) {
            System.arraycopy(rotated[i], 0, grille[i], 0, size);
        }
    }

    /**
     * Get column order based on the alphabetical order of the key
     */
    private static int[] getColumnOrder(String key) {
        int[] order = new int[key.length()];

        // Create pairs of (character, position)
        List<Map.Entry<Character, Integer>> pairs = new ArrayList<>();
        for (int i = 0; i < key.length(); i++) {
            pairs.add(new AbstractMap.SimpleEntry<>(key.charAt(i), i));
        }

        // Sort by character
        pairs.sort(Map.Entry.comparingByKey());

        // Extract the order
        for (int i = 0; i < pairs.size(); i++) {
            order[i] = pairs.get(i).getValue();
        }

        return order;
    }

    /**
     * Read text from a file
     */
    private static String readFromFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + e.getMessage());
        }
        return content.toString().trim();
    }

    /**
     * Write text to a file
     */
    private static void writeToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Помилка запису у файл: " + e.getMessage());
        }
    }
}
