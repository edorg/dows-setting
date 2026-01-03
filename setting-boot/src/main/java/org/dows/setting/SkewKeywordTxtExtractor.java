package org.dows.setting;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 PDFBox 3.0.5：
 * 1. 剔除旋转角度 > 10° 的文字
 * 2. 剔除含指定关键字的整行
 * 3. 删除多余空行
 */
@Slf4j
@Component
public class SkewKeywordTxtExtractor extends PDFTextStripper {

    /**
     * 需要剔除的关键字（大小写不敏感）
     */
    private final Set<String> keywords;

    public SkewKeywordTxtExtractor(Collection<String> keywords) {
        super();
        this.keywords = keywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        // 强制按坐标排序，避免文本乱序
        //setSortByPosition(true);
        // 关闭 stripper 自带的分页空白
        setLineSeparator("");
        //setArticleEnd(" ");
        setParagraphEnd("\r");
        //setPageEnd("\r\n");
        //setParagraphStart("\r");
    }

    @Override
    protected void writeString(String text, List<TextPosition> positions) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (TextPosition tp : positions) {
            double rad = Math.atan2(tp.getTextMatrix().getShearY(), tp.getTextMatrix().getScaleY());
            double deg = Math.toDegrees(rad);
            // 只保留 <=1°
            if (Math.abs(deg) <= 1) {
                sb.append(tp.getUnicode());
            }
        }
        String line = sb.toString().trim();
        // 空行直接丢弃
        if (line.isEmpty()) return;

        // 关键字过滤（大小写不敏感）
        String lineLower = line.toLowerCase();
        for (String kw : keywords) {
            // 命中关键字 -> 丢弃整行
            if (lineLower.contains(kw)) return;
        }

        // 压缩段落内部多余换行 -> 单空格
        line = line.replaceAll("\\s+", " ");
        if (!line.isEmpty()) {
            output.write(line);
        }
    }

    public static void main(String[] args) throws IOException {
        String pdfPath = "E:\\download\\download\\resume\\后端\\51job_程柏松_后端技术合伙人_上海(5826206).pdf";
        String outputPath = "E:\\download\\download\\resume\\后端\\51job_程柏松_后端技术合伙人_上海(5826206)_clean.txt";

        PDDocument doc = Loader.loadPDF(new File(pdfPath));

        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8);

        List<String> list = Arrays.asList("仅供招聘专用，企业应尽保密义务，禁止外传", "一经发现我司有权采取一切必要措施，包括但不限于暂停或终止服务。");

        SkewKeywordTxtExtractor stripper = new SkewKeywordTxtExtractor(list);
        stripper.writeText(doc, w);
        Path file = Paths.get(outputPath);
        try {
            // 2. 高效读取：一次性读全文件为字符串（1次IO，底层带缓冲）
            String content = Files.readString(file, StandardCharsets.UTF_8);

            // 3. 剔除所有换行符：匹配 \r\n、\n、\r，直接替换为空（无冗余空格）
            String contentWithoutLineBreaks = content.replaceAll("[\r|\n]+", "\r");

            // 4. 高效写回：一次性覆盖原文件（1次IO，底层带缓冲）
            Files.writeString(file, contentWithoutLineBreaks, StandardCharsets.UTF_8);

            System.out.println("✅ 换行符已彻底剔除！文件路径：");
        } catch (Exception e) {
            System.err.println("❌ 处理失败：" + e.getMessage());
        }
        /*StringWriter sw = new StringWriter();
        //stripper.setSortByPosition(true);
        stripper.writeText(doc,sw);
        String oneLine = sw.toString().replaceAll("\r\n", "");
        Files.writeString(Path.of(outputPath), oneLine, StandardCharsets.UTF_8);*/
    }

}