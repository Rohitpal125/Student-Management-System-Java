package util;

import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

// Plain Java helpers for the AI feature (no Spring, easy to test)
public class AiHelper {

    // Turns the student list into plain numbers.
    // No names, emails or phone numbers are included, on purpose.
    public static String buildStats(List<Student> students) {

        if (students == null || students.isEmpty()) {
            return "";
        }

        double total = 0;
        int below = 0;
        Map<String, List<Student>> byCourse = new TreeMap<>();

        for (Student s : students) {
            total += s.getCgpa();
            if (s.getCgpa() < 6.0) {
                below++;
            }
            byCourse.computeIfAbsent(String.valueOf(s.getCourse()), k -> new ArrayList<>()).add(s);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total students: ").append(students.size()).append("\n");
        sb.append(String.format(Locale.US, "Average CGPA overall: %.2f", total / students.size())).append("\n");
        sb.append("Students with CGPA below 6.0: ").append(below).append("\n\n");
        sb.append("By course:\n");

        for (Map.Entry<String, List<Student>> entry : byCourse.entrySet()) {
            List<Student> group = entry.getValue();

            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            int groupBelow = 0;

            for (Student s : group) {
                sum += s.getCgpa();
                min = Math.min(min, s.getCgpa());
                max = Math.max(max, s.getCgpa());
                if (s.getCgpa() < 6.0) {
                    groupBelow++;
                }
            }

            sb.append(String.format(Locale.US,
                    "- %s: %d students, average CGPA %.2f, lowest %.1f, highest %.1f, below 6.0: %d",
                    entry.getKey(), group.size(), sum / group.size(), min, max, groupBelow));
            sb.append("\n");
        }

        return sb.toString();
    }

    // The instruction we send to the AI, together with the numbers
    public static String buildPrompt(String stats) {
        return "You are an assistant for a college administrator.\n"
                + "Use ONLY the statistics below. Do not invent numbers, names or facts.\n"
                + "Write a short report in plain English, under 120 words:\n"
                + "1. Two observations.\n"
                + "2. Two practical suggestions.\n"
                + "Do not use markdown symbols such as ** or #.\n\n"
                + "Statistics:\n" + stats;
    }

    // Pulls the AI's text out of the JSON reply:
    // candidates[0].content.parts[*].text
    // Returns null if the reply has no text.
    @SuppressWarnings("unchecked")
    public static String extractText(Map<String, Object> response) {
        try {
            List<Object> candidates = (List<Object>) response.get("candidates");
            Map<String, Object> first = (Map<String, Object>) candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) first.get("content");
            List<Object> parts = (List<Object>) content.get("parts");

            StringBuilder sb = new StringBuilder();

            for (Object part : parts) {
                Map<String, Object> p = (Map<String, Object>) part;

                // Skip the model's private "thinking" parts, if any
                if (Boolean.TRUE.equals(p.get("thought"))) {
                    continue;
                }

                Object text = p.get("text");
                if (text != null) {
                    sb.append(text);
                }
            }

            String result = sb.toString().trim();
            return result.isEmpty() ? null : result;

        } catch (Exception e) {
            return null;
        }
    }
}