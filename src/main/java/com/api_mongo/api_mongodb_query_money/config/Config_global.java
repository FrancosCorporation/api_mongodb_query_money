package com.api_mongo.api_mongodb_query_money.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import com.api_mongo.api_mongodb_query_money.models.Models_data_b3_names;
import com.api_mongo.api_mongodb_query_money.models.Models_data_list_names;
import com.api_mongo.api_mongodb_query_money.models.Models_data_b3;
import com.api_mongo.api_mongodb_query_money.services.Services_data_b3_names;
import com.api_mongo.api_mongodb_query_money.services.Services_data_list_names;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class Config_global {
    public static final String DATETIME_FORMAT = "HH:mm:ss' 'dd-MM-yyyy' '";
    public static LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(
            DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    public final Services_data_b3_names services_data_b3_names;
    public final Services_data_list_names services_data_list_names;

    public Config_global(Services_data_b3_names services_data_b3_names,
            Services_data_list_names services_data_list_names) {
        this.services_data_b3_names = services_data_b3_names;
        this.services_data_list_names = services_data_list_names;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return new ObjectMapper().registerModule(module);
    }

    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        ClassPathResource classPathResource = new ClassPathResource("src/main/resources/static");
        List<Resource> locations = Arrays.asList(classPathResource);
        requestHandler.setLocations(locations);
        return requestHandler;
    }

    @Bean
    public void insereArquivos() {
        System.out.println(new SimpleDateFormat("yyyy/MM/dd").format(new Date())); // 2022/07/20
        rotina();

    }

    public void rotina() {
        new Thread() {

            @Override
            public void run() {
                String way = "src/main/resources/python/dadosBrutos.csv ";
                String mark = "Date;Open;High;Low;Close;Price;Volume;";
                String line = "";
                String nameAct = "";
                List<Models_data_b3> listmodelData = new ArrayList<Models_data_b3>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(way));
                    while (true) {
                        line = br.readLine();
                        if (line != null) {
                            if (line.length() <= 10) {
                                if (listmodelData.isEmpty()) {
                                    nameAct = line.replace(";", "");
                                    continue;
                                } else {
                                    if (!services_data_b3_names.findDataByNameActionExist(nameAct)) {
                                        Models_data_b3_names nameActionData = new Models_data_b3_names();
                                        nameActionData.setNameAction(nameAct);
                                        nameActionData.setId(UUID.randomUUID());
                                        nameActionData.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
                                        nameActionData.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
                                        nameActionData.setDateandprices(listmodelData);
                                        services_data_list_names.saveNewList(
                                                new Models_data_list_names(nameActionData.getId(), nameAct));
                                        services_data_b3_names.saveNewData(nameActionData);
                                        listmodelData.clear();
                                        nameAct = line.replace(";", "");

                                    } else {
                                        listmodelData.clear();
                                        continue;
                                    }

                                }
                            } else if (!(line.equals(mark))) {
                                /*
                                 * private String Date;
                                 * private String Open;
                                 * private String High;
                                 * private String Low;
                                 * private String Close;
                                 * private String Price;
                                 * private String Volume;
                                 */
                                Models_data_b3 model = new Models_data_b3();
                                String[] lista = line.split(";");
                                model.setDate(lista[0]);
                                model.setOpen(lista[1]);
                                model.setHigh(lista[2]);
                                model.setLow(lista[3]);
                                model.setClose(lista[4]);
                                model.setPrice(lista[5]);
                                model.setVolume(lista[6]);
                                listmodelData.add(model);

                            }
                            continue;
                        } else {
                            if (!services_data_b3_names.findDataByNameActionExist(nameAct)) {
                                Models_data_b3_names nameActionData = new Models_data_b3_names();
                                nameActionData.setNameAction(nameAct);
                                nameActionData.setId(UUID.randomUUID());
                                nameActionData.setDataCreate(LocalDateTime.now(ZoneId.of("UTC")));
                                nameActionData.setChangeDate(LocalDateTime.now(ZoneId.of("UTC")));
                                nameActionData.setDateandprices(listmodelData);
                                services_data_list_names
                                        .saveNewList(new Models_data_list_names(nameActionData.getId(), nameAct));
                                services_data_b3_names.saveNewData(nameActionData);
                                listmodelData.clear();
                                break;
                            } else {
                                listmodelData.clear();
                                break;
                            }

                        }
                    }
                    System.out.println("\n Salvou e Finalizou! \n");
                    br.close();
                } catch (Exception e) {
                    System.out.println("File not found" + e);
                }
            }
        }.start();
    }

    @Controller
    public static class FaviconController {

        @RequestMapping(value = "favicon.ico", method = RequestMethod.GET)
        @ResponseBody
        void favicon() {
        }
    }

}
