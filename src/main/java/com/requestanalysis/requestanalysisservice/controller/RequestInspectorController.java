package com.requestanalysis.requestanalysisservice.controller;

import com.requestanalysis.requestanalysisservice.factory.RequestDetailsFactory;
import com.requestanalysis.requestanalysisservice.model.RequestDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspect")
public class RequestInspectorController {

    @RequestMapping(value = "/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestDetails inspect(HttpServletRequest request, @RequestBody(required = false) String body) {
        return RequestDetailsFactory.from(request, body);
    }
}
