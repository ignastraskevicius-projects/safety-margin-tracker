package org.ignast.stockinvesting.api.controller;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({ Default.class, AllowedCharacters.class })
public interface FieldValidationSequence {
}

interface AllowedCharacters {
}
