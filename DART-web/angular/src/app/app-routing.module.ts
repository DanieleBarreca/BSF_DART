import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { LoginComponent } from './login/login.component';
import { ExistingQueriesComponent } from './existing-queries/existing-queries.component';
import { QueryResultComponent } from './query-result/query-result.component';
import { HomeGuard } from './home.guard';
import { QueriesGuard } from './queries.guard';
import { EmptyComponent } from './empty/empty.component';
import { GroupAdminComponent } from './group-admin/group-admin.component';
import { AdminGuard } from './admin.guard';
import {ReportsComponent} from "./reports/reports.component";
import {ReportResultComponent} from "./report-result/report-result.component";
import {ReportViewerComponent} from "./report-viewer/report-viewer.component";


const routes: Routes = [
  {path: '', component:EmptyComponent, canActivate: [AuthGuard, HomeGuard],runGuardsAndResolvers:'always'},
  {path: 'queries', component: ExistingQueriesComponent, canActivate: [AuthGuard, QueriesGuard],runGuardsAndResolvers:'always'},
  {path: 'reports', component: ReportsComponent, canActivate: [AuthGuard, QueriesGuard],runGuardsAndResolvers:'always'},
  {path: 'query', component: QueryResultComponent, canActivate: [AuthGuard, QueriesGuard],runGuardsAndResolvers:'always' },
  {path: 'report', component: ReportResultComponent, canActivate: [AuthGuard, QueriesGuard] },
  {path: 'report-viewer', component: ReportViewerComponent },
  {path: 'group-admin', component: GroupAdminComponent, canActivate: [AuthGuard, AdminGuard] },
  {path: 'login', component: LoginComponent }
]

@NgModule({
  imports: [RouterModule.forRoot(routes,{onSameUrlNavigation:'reload'})],
  exports:[RouterModule]
})
export class AppRoutingModule { }
