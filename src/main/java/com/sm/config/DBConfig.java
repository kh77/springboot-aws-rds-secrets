package com.sm.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.google.gson.Gson;
import com.sm.config.response.AwsSecretsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretkey;

    @Bean
    public DataSource dataSource() {
        AwsSecretsResponse secrets = getSecret();
        return DataSourceBuilder
                .create()
                .url("jdbc:" + secrets.getEngine() + "://" + secrets.getHost() + ":" + secrets.getPort() + "/product_management")
                .username(secrets.getUsername())
                .password(secrets.getPassword())
                //.driverClassName("com.mysql.cj.jdbc.driver")
                .build();
    }


    private AwsSecretsResponse getSecret() {
        Gson gson = new Gson();
        String secretName = "rds-credential/dev";
        String region = "us-east-2";


        // Access Key and Secret Key can also be retrived from Secrets Manager using Other General Type
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretkey)))
                .build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);

        GetSecretValueResult getSecretValueResult;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            throw e;
        }

        String secret;
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            return gson.fromJson(secret, AwsSecretsResponse.class);
        }

        return null;
    }

}
