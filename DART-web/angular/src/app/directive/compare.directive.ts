import { Directive, Attribute  } from '@angular/core';
import { Validator,  NG_VALIDATORS, ValidationErrors, AbstractControl } from '@angular/forms';


@Directive({
  selector: '[compareTo]',
  providers: [{provide: NG_VALIDATORS, useExisting: CompareDirective, multi: true}]
})
export class CompareDirective implements Validator {

  constructor(@Attribute('compareTo') public otherControlName: string,
              @Attribute('parent') public parent: string){}

  validate(thisControl: AbstractControl): ValidationErrors {    

    let otherControl = thisControl.root.get(this.otherControlName);

    // value not equal in verify control
    if (otherControl && 
        thisControl.value !== otherControl.value && 
        !this.isParent) {
      return {"compare": true};
    }

    // user typing in password and match
    if (otherControl && 
        thisControl.value === otherControl.value && 
        this.isParent) {
        delete otherControl.errors['compare'];
        if (!Object.keys(otherControl.errors).length) otherControl.setErrors(null);
    }

    // user typing in password and mismatch
    if (otherControl && thisControl.value !== otherControl.value && this.isParent) {
        otherControl.setErrors({ "compare": true });
    }
  }

  private get isParent() {
    if (!this.parent) 
      return false;

    return this.parent === 'true' ? true: false;
  }
}  
