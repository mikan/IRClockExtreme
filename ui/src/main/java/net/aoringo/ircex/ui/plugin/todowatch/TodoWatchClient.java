/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.todowatch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.aoringo.ircex.net.HttpClient;
import net.aoringo.ircex.ui.plugin.todowatch.entity.Todo;

/**
 *
 * @author mikan
 */
public class TodoWatchClient {
    
    private static final Logger LOG = Logger.getLogger(TodoWatchClient.class.getName());
    private static final String ENDPOINT = "http://54.65.21.152:8080/TodoWatch/";
    private static final String API_LOGIN = "stlogin";
    private static final String API_LIST = "json";
    private static final String CONTENT_TYPE = "application/json";
    private final String user;
    private final String password;
    
    public TodoWatchClient(String user, String password) {
        this.user = user;
        this.password = password;
    }
    
    public List<Todo> getList() throws IOException {
        String params = "{\"user\":\""+ user +"\",\"passwd\":\""+ password +"\"}";
        HttpClient client = new HttpClient();
        String res = client.requestPost(ENDPOINT + API_LOGIN, CONTENT_TYPE, params);
        LOG.log(Level.INFO, "TodoWatch login response: {0}", res);
        String json = client.requestGet(ENDPOINT + API_LIST);
        LOG.log(Level.INFO, "TodoWatch list response: {0}", json);
        ObjectMapper mapper = new ObjectMapper();
        List<Todo> todos = mapper.readValue(json, new TypeReference<List<Todo>>() {});
        List<Todo> filtered = new ArrayList<>();
        todos.stream().filter(todo -> todo.getTitle() != null).forEach(
                todo -> filtered.add(todo));
        return filtered;
    }
}
