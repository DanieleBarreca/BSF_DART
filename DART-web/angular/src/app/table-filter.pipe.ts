import { Pipe, PipeTransform, Injectable } from '@angular/core';

@Pipe({
  name: 'tableFilter'
})

@Injectable()
export class TableFilter implements PipeTransform {

  transform(items: any[], field: string, value: string): any[] {

    if (!items) return [];

    if (!field || !value) return items;

    

    return items.filter(singleItem => this.fieldFilter(singleItem[field],value));
    
  }

  private fieldFilter(fieldValue: any, filter: string): boolean{
    if (fieldValue instanceof Array) {
      return fieldValue.filter(subFieldValue => this.fieldFilter(subFieldValue,filter)).length >0;
    }

    return fieldValue.toLowerCase().includes(filter.toLowerCase());

  }

}


