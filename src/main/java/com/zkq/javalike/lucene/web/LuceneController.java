package com.zkq.javalike.lucene.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 参考 
 * @author zkq
 *
 */
@Controller
public class LuceneController {
	
    @RequestMapping("/add")
    @ResponseBody
    public String welcomepage(Map<String, Object> model) {

        try {
            Indexer indexer = new Indexer();
            indexer.addEntity();

            model.put("message", "Success");
        } catch (IOException ex) {
            model.put("message", "Failure");
        }

        return "welcome";
    }

    @RequestMapping("/file")
    @ResponseBody
    public String fileindex(Map<String, Object> model) {

        try {
            Indexer indexer = new Indexer();
            indexer.addFile();

            model.put("message", "SuccessF");
        } catch (IOException ex) {
            model.put("message", "FailureF");
        }

        return "welcome";
    }

    @RequestMapping("/searchText")
    @ResponseBody
    public String searchindex(Map<String, Object> model) {

        try {
            Indexer indexer = new Indexer();
            List<String> rlts = indexer.SearchFiles();
            String message = "";
            for (String str : rlts) {
                message += str + " \r\n";
            }
            model.put("message", message);
        } catch (Exception ex) {
            model.put("message", "FailureF");
        }

        return "welcome";
    }
}