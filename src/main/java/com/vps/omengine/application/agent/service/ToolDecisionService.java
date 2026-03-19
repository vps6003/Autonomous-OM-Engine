package com.vps.omengine.application.agent.service;

import org.springframework.stereotype.Component;

@Component
public class ToolDecisionService {

    public String decideTool(String input){
        if(input.toLowerCase().contains("order"))
            return "create_order";

        return "search_products";
    }
}
