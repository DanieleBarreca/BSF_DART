import { Directive } from "@angular/core";
import { NG_VALIDATORS, Validator, AbstractControl, ValidationErrors } from "@angular/forms";

@Directive({
    selector: '[validate-email]',
    providers: [{provide: NG_VALIDATORS, multi:true, useExisting:EmailDirective}]
})
export class EmailDirective implements Validator {
    emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    validate(c: AbstractControl): ValidationErrors {
        if (c.value == null)
            return null;

        if (!this.emailPattern.test(c.value)) {
            return { "pattern": true };
        }
        
        return null;
    }

}