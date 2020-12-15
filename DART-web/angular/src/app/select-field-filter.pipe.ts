import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'selectFieldFilter'
})
export class SelectFieldFilterPipe implements PipeTransform {

  transform(value: any, fieldToSelect: string): any {
    if (!value) return null;

    if (!fieldToSelect) return value;

    return fieldToSelect.split('.').reduce((a,b) => (a!=undefined) ? a[b] : a, value);
  }

}
