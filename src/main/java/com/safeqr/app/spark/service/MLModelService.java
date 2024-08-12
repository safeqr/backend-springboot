package com.safeqr.app.spark.service;

import com.safeqr.app.qrcode.model.URLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class MLModelService {
    private static final Logger logger = LoggerFactory.getLogger(MLModelService.class);

    public MLModelService() {

    }


    public String predict(URLModel urlModel) {

        return "haha";
    }
}
