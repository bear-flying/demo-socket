package cn.itcast.haoke.dubbo.api.graphql;

import cn.itcast.haoke.dubbo.api.service.HouseResourcesService;
import cn.itcast.haoke.dubbo.server.pojo.HouseResources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

// 实现的功能：将GraphQL对象载入到Spring容器，并且完成GraphQL对象的初始化的功能
@Component
public class GraphQLProvider {

    private GraphQL graphQL;

    @Autowired
    private HouseResourcesService houseResourcesService;

    @Autowired
    private List<MyDataFetcher> myDataFetchers;

    //实现对GraphQL对象的初始化
    @PostConstruct
    public void init() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:haoke.graphqls");
        this.graphQL = GraphQL.newGraphQL(buildGraphQLSchema(file)).build();

    }

    private GraphQLSchema buildGraphQLSchema(File file) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(file);
        return new SchemaGenerator().makeExecutableSchema(typeRegistry, buildWiring());
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                /*
                以后每当增加查询时，都需要修改该方法，如果查询方法很多的话，那么这个方法将变得非常难以维护 所以需要改进
                改进思路：
                1，编写接口
                2，所有实现查询的逻辑都实现该接口
                3，在GraphQLProvider中使用该接口的实现类进行处理
                4，以后需要新增查询逻辑只需要增加实现类即可

                .type("HaokeQuery", builder -> {
                            builder.dataFetcher("HouseResources",
                                    environment ->{
                                        Long id = environment.getArgument("id");
                                        return this.houseResourcesService.queryHouseResourcesById(id);
                                    });
                            return builder;
                        }

                )
                */
                .type("HaokeQuery", builder -> {
                            for (MyDataFetcher myDataFetcher : myDataFetchers) {
                                builder.dataFetcher(myDataFetcher.fieldName(),
                                        environment -> myDataFetcher.dataFetcher(environment));
                            }
                            return builder;
                        }

                )
                .build();

    }

    @Bean
    public GraphQL graphQL() {
        return this.graphQL;
    }

}
