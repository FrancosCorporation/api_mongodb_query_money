package com.api_mongo.api_mongodb_query_money.controllers;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client;
import com.api_mongo.api_mongodb_query_money.models.Models_layout_data_client_list;
import com.api_mongo.api_mongodb_query_money.services.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/wallet")
public class Controller_layout_client {

    private final Services_layout_data_client services_layout_data_client;
    private final Services_clients services_clients;
    private final Services_data_b3_names services_data_b3_names;
    // Models_layout_data_client

    public Controller_layout_client(Services_layout_data_client services_layout_data_client,
            Services_clients services_clients, Services_data_b3_names services_data_b3_names) {
        this.services_layout_data_client = services_layout_data_client;
        this.services_clients = services_clients;
        this.services_data_b3_names = services_data_b3_names;
    }

    @GetMapping("")
    public ResponseEntity<Object> AllLayouts() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_layout_data_client.getAllLayouts());
    }

    @GetMapping("/all")
    public ResponseEntity<Object> GetAllLayouts() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(services_data_b3_names.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> GetWalletById(@PathVariable(value = "id") UUID id) {

        Optional<Models_layout_data_client> models_layout_data_client = services_layout_data_client.findLayoutById(id);
        if (models_layout_data_client.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(models_layout_data_client.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> CreateAccount(@RequestBody HttpServletRequest request,
            Models_layout_data_client models_layout_data_client) throws IOException {

        System.out.println("json = " + request.getScheme());
        return null;

    }

    public ResponseEntity<Object> CreateNewWallet(
            @RequestBody @Valid Models_layout_data_client models_layout_data_client) {

        try {

            // verify thats id is valid
            if (services_clients.findClientForId(models_layout_data_client.getId()).isPresent()) {
                // verify thats Action name exist and set the list with thens
                models_layout_data_client = services_layout_data_client
                        .returnNameActionsExist(models_layout_data_client);
                if (models_layout_data_client.getActionsAndPrice().size() > 0) {
                    // verify thats wallet exist and return case exist
                    Optional<Models_layout_data_client> clientwallet = services_layout_data_client
                            .findLayoutById(models_layout_data_client.getId());
                    // verify thats null
                    if (clientwallet != null) {
                        // get the wallets olders
                        List<Models_layout_data_client_list> wallets = clientwallet.get().getActionsAndPrice();
                        // set the new wallets together
                        wallets.addAll(models_layout_data_client.getActionsAndPrice());
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(services_layout_data_client.saveNameActions(clientwallet.get()));
                    } else {
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(services_layout_data_client.saveNameActions(models_layout_data_client));

                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body("Any Names Actions Not Found");
                }

            } else {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Not Found");
            }

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Not Found");
        }

    }

}
