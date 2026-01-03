package org.dows.setting;

import cn.hutool.json.JSONUtil;
import io.vertx.core.AbstractVerticle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.dows.rade.event.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class RfaAnalysisVerticle extends AbstractVerticle {

    List<String> list = Arrays.asList("仅供招聘专用，企业应尽保密义务，禁止外传", "一经发现我司有权采取一切必要措施，包括但不限于暂停或终止服务。");
    @Value("${setting.download.folder:data}")
    private String ATTACHMENT_SAVE_FOLDER;

    @Override
    public void start() {
        vertx.eventBus().consumer("setting.mail.readed", message -> {
            try {
                String traceId = message.headers().get("traceId");
                TraceContext.set(traceId);
                Object body = message.body();
                extracted(body);
                log.info("body:{}", body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                TraceContext.clear();
            }
        });
    }

    private void extracted(Object body) throws IOException {
        log.info("rfa analysis verticle received event body：{}", JSONUtil.toJsonStr(body));
        AttachmentSchema attachmentSchema = JSONUtil.parseObj(body).get("data", AttachmentSchema.class);
        String filePath = attachmentSchema.getFilePath();
        PDDocument doc = Loader.loadPDF(new File(filePath));
        SkewKeywordTxtExtractor stripper = new SkewKeywordTxtExtractor(list);
        Path path = Paths.get(ATTACHMENT_SAVE_FOLDER, attachmentSchema.getFileName() + ".txt");
        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8);
        stripper.writeText(doc, w);
        doc.close();
    }

}
