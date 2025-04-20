import java.io.*;
import java.util.*;

public class ExpenseCategory {
    private Map<String, Double> categories;

    public ExpenseCategory() {
        categories = new HashMap<>();
        loadCategoriesFromFile();
    }

    // 加载分类信息
    private void loadCategoriesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("categories.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                categories.put(data[0], Double.parseDouble(data[1]));
            }
        } catch (IOException e) {
            System.out.println("Error loading categories: " + e.getMessage());
        }
    }

    // 保存分类信息到CSV文件
    public void saveCategoriesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("categories.csv"))) {
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving categories: " + e.getMessage());
        }
    }

    // 添加新的分类
    public void addCategory(String name, double amount) {
        categories.put(name, amount);
        saveCategoriesToFile();
    }

    // 修改分类
    public void modifyCategory(String name, double newAmount) {
        if (categories.containsKey(name)) {
            categories.put(name, newAmount);
            saveCategoriesToFile();
        } else {
            System.out.println("Category not found!");
        }
    }

    // 删除分类
    public void removeCategory(String name) {
        categories.remove(name);
        saveCategoriesToFile();
    }

    public Map<String, Double> getCategories() {
        return categories;
    }
}
