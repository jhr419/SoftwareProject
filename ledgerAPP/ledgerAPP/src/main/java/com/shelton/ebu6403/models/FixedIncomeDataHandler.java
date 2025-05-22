package com.shelton.ebu6403.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FixedIncomeDataHandler {
    private static final String FILE_PATH = "resources/fixedincome.csv";

    // 保存固定收入到 CSV 文件
    public void saveFixedIncomes(List<FixedIncome> fixedIncomes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (FixedIncome income : fixedIncomes) {
                writer.write(income.getSource() + "," + income.getAmount() + "," + income.getPeriod());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("保存固定收入时出错: " + e.getMessage());
        }
    }

    // 从 CSV 文件加载固定收入
    public List<FixedIncome> loadFixedIncomes() {
        List<FixedIncome> fixedIncomes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String source = data[0];
                    double amount = Double.parseDouble(data[1]);
                    String period = data[2];
                    fixedIncomes.add(new FixedIncome(source, amount, period));
                }
            }
        } catch (FileNotFoundException e) {
            // 如果文件不存在，忽略错误（可能是第一次运行程序）
        } catch (IOException e) {
            System.err.println("加载固定收入时出错: " + e.getMessage());
        }
        return fixedIncomes;
    }
}