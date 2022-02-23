package org.ignast.stockinvesting.api.controller;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({ Default.class, AdherenceToStandard.class })
public interface FieldValidationSequence {
}

interface AdherenceToStandard {

}