package cn.itcast.haoke.dubbo.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 只能处理写死参数的查询的请求
 * 示例：
 * {
 *  HouseResources(id: 8) {
 *      id
 *      title
 *      estateId
 *      buildingUnit
 *      buildingFloorNum
 *      mobile
 *      useArea
 *      pic
 *  }
 * }
 */
@RequestMapping("graphqlold")
@Controller
@CrossOrigin
public class GraphQLOldController {

    @Autowired
    private GraphQL graphQL;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    /**
     * 实现GraphQL查询
     *
     * @param query
     * @return
     */
    @GetMapping
    @ResponseBody
    public Map<String, Object> getQueryOld(@RequestParam("query") String query) {
        return this.graphQL.execute(query).toSpecification();
    }

    /**
     * POST请求时，传到后台的是一个Json，即与get接收到的请求参数有所不同，
     * 需要我们的代码把有用的信息解析出来。
     *
     * @param json
     * @return
     */
    @PostMapping
    @ResponseBody
    public Map<String, Object> postQueryOld(@RequestBody String json) {

        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            if(jsonNode.has("query")){
                String query = jsonNode.get("query").textValue();
                return this.graphQL.execute(query).toSpecification();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("msg", "查询出错");
        return error;
    }

}
