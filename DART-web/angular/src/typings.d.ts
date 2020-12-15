/* SystemJS module definition */
declare var module: NodeModule;
interface NodeModule {
  id: string;
}

interface JQuery {
  queryBuilder(method: string, data?: any, options?:any) : any;
  queryBuilder(options?: any) : any;
  sortable(options?:any) :any;
  selectize(options?: any):any;
  on(event: string, fun: ()=> void);
}
