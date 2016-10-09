package rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodbarber.premierleaguene.domain.Category;
import com.goodbarber.premierleaguene.repository.ConnectionManager;
import com.goodbarber.premierleaguene.spring.SpringConfig;

import com.goodbarber.premierleaguene.utils.ProjectConfig;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringConfig.class})
public class CategoryServiceTest {
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void before() throws Exception {
        ProjectConfig.load();
        ConnectionManager.init();
    }

    @Ignore
    @Test
    public void addCategory() throws Exception {
        MockMvc mockMvc = webAppContextSetup(this.wac).build();
        ResultActions result;

        Category category = new Category();
        category.name = "name";
        category.rssFeed = "http://golazogoal.com/feed/";

        result = mockMvc.perform(put("category").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(category)));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
        TypeReference<Category> type = new TypeReference<Category>() {};
        category = new ObjectMapper().readValue(json, type);
        assertNotNull(category);
        assertEquals("name", category.name);
        assertEquals("http://golazogoal.com/feed/", category.rssFeed);
    }

    @Ignore
    @Test
    public void getCategory() throws Exception {
        MockMvc mockMvc = webAppContextSetup(this.wac).build();
        ResultActions result;

        result = mockMvc.perform(get("/category/name/get").param("name", "name"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
        TypeReference<Category> type = new TypeReference<Category>() {};
        Category category = new ObjectMapper().readValue(json, type);
        assertNotNull(category);
        assertEquals("name", category.name);
        assertEquals("http://golazogoal.com/feed/", category.rssFeed);
    }

    @Ignore
    @Test
    public void testCategoryList() throws Exception {
        MockMvc mockMvc = webAppContextSetup(this.wac).build();
        ResultActions result;

        result = mockMvc.perform(get("/category/list").param("start", "0").param("limit", "20"));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
        TypeReference<List<Category>> type = new TypeReference<List<Category>>() {};
        List<Category> employees = new ObjectMapper().readValue(json, type);
        assertNotNull(employees);
        assertTrue(employees.size() > 0);
    }

    @Ignore
    @Test
    public void updateCategory() throws Exception {
        MockMvc mockMvc = webAppContextSetup(this.wac).build();
        ResultActions result;

        Category category = new Category();
        category.name = "all1";
        category.rssFeed = "http://golazogoal.com/feed/";

        result = mockMvc.perform(post("/category/all").content(new ObjectMapper().writeValueAsString(category)));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
        String json = result.andReturn().getResponse().getContentAsString();
        TypeReference<Category> type = new TypeReference<Category>() {};
        category = new ObjectMapper().readValue(json, type);
        assertNotNull(category);
        assertEquals("name", category.name);
        assertEquals("http://golazogoal.com/feed/", category.rssFeed);
    }

    @Ignore
    @Test
    public void deleteCategory() throws Exception {
        MockMvc mockMvc = webAppContextSetup(this.wac).build();
        ResultActions result;

        result = mockMvc.perform(delete("/category/name2"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$").exists());
    }

}
