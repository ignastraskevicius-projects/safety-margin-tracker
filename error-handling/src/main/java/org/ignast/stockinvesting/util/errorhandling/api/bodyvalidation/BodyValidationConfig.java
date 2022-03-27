package org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation;

import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.parsing.ParsingValidationConfig;
import org.ignast.stockinvesting.util.errorhandling.api.bodyvalidation.postparsed.PostParsedValidationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ParsingValidationConfig.class, PostParsedValidationConfig.class })
public class BodyValidationConfig {}
