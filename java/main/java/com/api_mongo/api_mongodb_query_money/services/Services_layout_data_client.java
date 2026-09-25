package com.api_mongo.api_mongodb_query_money.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client;
import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client_list;
import com.api_mongo.api_mongodb_query_money.repositories.Repository_layout_data_client;

@Service
public class Services_layout_data_client {
    private final Repository_layout_data_client repository_layout_data_client;
    private final Services_data_b3_names services_data_b3_names;

    public Services_layout_data_client(Repository_layout_data_client repository_layout_data_client,
            Services_data_b3_names services_data_b3_names) {
        this.repository_layout_data_client = repository_layout_data_client;
        this.services_data_b3_names = services_data_b3_names;
    }

    public List<Models_layout_data_client> getAllLayouts() {
        return repository_layout_data_client.findAll();
    }

    public Optional<Models_layout_data_client> findLayoutById(UUID id) {
        try {
            Optional<Models_layout_data_client> models_layout_data_client = repository_layout_data_client
                    .findById(id);
            if (models_layout_data_client.isPresent()) {
                return models_layout_data_client;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public Models_layout_data_client saveNameActions(Models_layout_data_client models_layout_data_client) {
        try {
            return repository_layout_data_client.save(models_layout_data_client);

        } catch (Exception e) {
            return null;
        }
    }

    public Models_layout_data_client returnNameActionsExist(Models_layout_data_client models_layout_data_client) {
        try {
            List<Models_layout_data_client_list> newModel = new ArrayList<Models_layout_data_client_list>();
            for (int i = 0; i < models_layout_data_client.getActionsAndPrice().size(); i++) {
                if (services_data_b3_names.findDataByNameActionExist(
                        models_layout_data_client.getActionsAndPrice().get(i).getNameAction())) {
                    newModel.add(models_layout_data_client.getActionsAndPrice().get(i));
                } else {
                    break;
                }
            }
            models_layout_data_client.setActionsAndPrice(newModel);

            return models_layout_data_client;

        } catch (Exception e) {
            return null;
        }
    }

}
