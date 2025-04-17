import java.io.*;
import java.util.*;

public class ExpenseCategory {
    private Map<String, Double> categories;  // 用于存储分类信息，key 为分类名称，value 为分类对应的金额

    // 构造函数，初始化分类数据并从文件加载分类信息
    public ExpenseCategory() {
        categories = new HashMap<>();
        loadCategoriesFromFile();  // 从文件加载分类信息
    }

    // 从文件加载分类信息
    private void loadCategoriesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("categories.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                categories.put(data[0], Double.parseDouble(data[1])); // 将每行数据加载到 categories 映射中
            }
        } catch (IOException e) {
            System.out.println("加载分类信息时出错: " + e.getMessage());
        }
    }

    // 保存分类信息到CSV文件
    public void saveCategoriesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("categories.csv"))) {
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue()); // 将每个分类的名称和金额写入文件
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存分类信息时出错: " + e.getMessage());
        }
    }

    // 添加新的分类
    public void addCategory(String name, double amount) {
        categories.put(name, amount);  // 将新的分类添加到映射中
        saveCategoriesToFile();  // 更新文件
    }

    // 修改现有分类的金额
    public void modifyCategory(String name, double newAmount) {
        if (categories.containsKey(name)) {
            categories.put(name, newAmount);  // 修改分类金额
            saveCategoriesToFile();  // 更新文件
        } else {
            System.out.println("未找到该分类!");
        }
    }

    // 删除分类
    public void removeCategory(String name) {
        categories.remove(name);  // 从映射中删除该分类
        saveCategoriesToFile();  // 更新文件
    }

    // 获取所有分类信息
    public Map<String, Double> getCategories() {
        return categories;
    }
}
