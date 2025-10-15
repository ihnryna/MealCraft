package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@EnableConfigurationProperties(ExternalRecipesProperties.class)
@ConditionalOnProperty(prefix = "mealcraft.recipes.external", name = "enabled", havingValue = "true")
@ConditionalOnClass({ RestClient.class, ObjectMapper.class })
public class ExternalRecipesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean()
    public RecipeProvider recipeProvider(RestClient.Builder builder,
                                         ObjectMapper mapper,
                                         ExternalRecipesProperties props) {
        String url = props.getUrl();
        RestClient client = builder.baseUrl(url).build();
        return new ExternalRecipeService(client, mapper, url);
    }
}