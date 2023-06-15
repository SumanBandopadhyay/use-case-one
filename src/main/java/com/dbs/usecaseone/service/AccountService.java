package com.dbs.usecaseone.service;

import com.dbs.usecaseone.model.Account;
import com.dbs.usecaseone.model.AccountAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class AccountService {

    public static final String OUTPUT_DAT_ABSOLUTE_PATH = "/Users/suman/manvmachine/use-case-one/src/main/resources/output.dat";
    public static final String ACCOUNT_REQUEST_SCHEMA_PATH = "/Users/suman/manvmachine/use-case-one/src/main/resources/account_request_schema.json";
    public static final String ACCOUNT_RESPONSE_SCHEMA_PATH = "/Users/suman/manvmachine/use-case-one/src/main/resources/account_response_schema.json";

    // Convert data from one schema to another
    public String convert(JsonSchema requestSchema, JsonSchema responseSchema, String data) {

        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the source JSON string
            JsonNode sourceNode = objectMapper.readTree(data);

            // Validate the source JSON against the requestSchema
            requestSchema.validate(sourceNode);

            // Convert the sourceNode to list of Account objects
            List<Account> accounts = objectMapper.convertValue(sourceNode, objectMapper.getTypeFactory().constructCollectionType(List.class, Account.class));

            // Accounts list to list of AccountAttributes objects
            List<AccountAttributes> accountAttributes = accounts.stream()
                    .map(account -> {
                        // Create accountNumber_accountType_currency string
                        String accountNumber_accountType_currency = account.accountNumber() + "_" + account.accountType() + "_" + account.currency();

                        return AccountAttributes.builder()
                                .accountNumber(accountNumber_accountType_currency)
                                .panNbr(account.panNbr())
                                .overdraft_ind(account.overdraft_ind())
                                .build();
                    })
                    .toList();

            // Validate the accountAttributes list against the response schema
            JsonNode outputJson = objectMapper.valueToTree(accountAttributes);
            responseSchema.validate(outputJson);

            // Convert the transformed JsonNode back to JSON string
            return objectMapper.writeValueAsString(outputJson);
        } catch (JsonProcessingException | ProcessingException e) {
            e.printStackTrace();
        }

        return null;

    }

    public JsonSchema getJsonSchema(String schemaFileName) throws ProcessingException, IOException {
        // Create JsonSchemaFactory instance
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();

        // Load the JSON Schema from the file
        File schemaFile = new File(schemaFileName);

        JsonNode jsonNode = new ObjectMapper().readTree(schemaFile);

        // Create JsonSchema instance
        return schemaFactory.getJsonSchema(jsonNode);
    }

    public String convertAccountJson(String data) throws ProcessingException, IOException {
        // Get the request schema
        JsonSchema requestSchema = getJsonSchema(ACCOUNT_REQUEST_SCHEMA_PATH);

        // Get the response schema
        JsonSchema responseSchema = getJsonSchema(ACCOUNT_RESPONSE_SCHEMA_PATH);

        // Convert the data
        return convert(requestSchema, responseSchema, data);
    }

    public Resource convertStringToDatFile(String data) throws IOException, ProcessingException {
        String output = convertAccountJson(data);

        writeDatFile(output);

        return getDatFile();
    }

    private void writeDatFile(String output) throws IOException {
        // Write the JSON string to a DAT file
        FileWriter fileWriter = new FileWriter(OUTPUT_DAT_ABSOLUTE_PATH);
        fileWriter.write(output);
        fileWriter.close();
    }

    public Resource getDatFile() {
        return new FileSystemResource(OUTPUT_DAT_ABSOLUTE_PATH);
    }

}
