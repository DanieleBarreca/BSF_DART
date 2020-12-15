import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PopoverModule } from 'ngx-bootstrap/popover'
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { ModalModule } from 'ngx-bootstrap/modal';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import {ButtonsModule} from "ngx-bootstrap";

import { VcfService } from './vcf.service';
import { VcfTableComponent } from './vcf-table/vcf-table.component';
import { TableFilter } from './table-filter.pipe';
import { AppRoutingModule } from './app-routing.module';
import { VariantFilterQueryComponent } from './variant-filter-query/variant-filter-query.component';
import { VariantService } from './variant.service';
import { SelectFieldFilterPipe } from './select-field-filter.pipe';
import { VariantTableConfigComponent } from './variant-table-config/variant-table-config.component';
import { VariantDetailComponent } from './variant-detail/variant-detail.component';
import { IgvComponent } from './igv/igv.component';
import { FrequencyChartComponent } from './frequency-chart/frequency-chart.component';
import { ImpactChartComponent } from './impact-chart/impact-chart.component';
import {ChartsModule} from 'ng2-charts';
import { AuthenticationService } from './authentication.service';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth.guard';
import { LolliplotComponent } from './lolliplot/lolliplot.component';
import { ExternalProvidersService } from './external-providers.service';
import { QueryService } from './query.service';
import {ExistingQueriesComponent} from './existing-queries/existing-queries.component';
import { VariantQueryContainerComponent } from './variant-query-container/variant-query-container.component';
import { BedService } from './bed.service';
import { BedTableComponent } from './bed-table/bed-table.component';
import { AggregationsService } from './aggregations.service';
import { QueryModalComponent } from './query-modal/query-modal.component';
import { QueryResultComponent } from './query-result/query-result.component';
import { FileService } from './file.service';
import { PresetModalComponent } from './preset-modal/preset-modal.component';
import { PresetService } from './preset.service';
import { QueryBuilderComponent } from './query-builder/query-builder.component';
import { QueryPresetsComponent } from './query-presets/query-presets.component';
import { GenePanelPresetsComponent } from './gene-panel-presets/gene-panel-presets.component';
import { EmptyComponent } from './empty/empty.component';
import { HomeGuard } from './home.guard';
import { QueriesGuard } from './queries.guard';
import { UserComponent } from './user/user.component';
import { UserChangePasswordComponent } from './user-change-password/user-change-password.component';
import { EmailDirective } from './directive/email.directive';
import { CompareDirective } from './directive/compare.directive';
import { UserService } from './user.service';
import { GroupAdminComponent } from './group-admin/group-admin.component';
import { GroupUserManagementComponent } from './group-user-management/group-user-management.component';
import { GroupUserAddComponent } from './group-user-add/group-user-add.component';
import { AdminGuard } from './admin.guard';
import { GroupAdminService } from './group-admin.service';
import { GroupVcfManagementComponent } from './group-vcf-management/group-vcf-management.component';
import {
  BedGenomeModalComponent,
  GroupBedManagementComponent
} from './group-bed-management/group-bed-management.component';
import { VariantAgGridComponent } from './variant-ag-grid/variant-ag-grid.component';
import {AgGridModule} from "ag-grid-angular";
import { CoverageAgGridComponent } from './coverage-ag-grid/coverage-ag-grid.component';
import { VariantAgGridTrioComponent } from './variant-ag-grid-trio/variant-ag-grid-trio.component';
import { VariantAgGridPathogenicityComponent } from './variant-ag-grid-pathogenicity/variant-ag-grid-pathogenicity.component';
import { ConditionSelectComponent } from './condition-select/condition-select.component';
import {AnnotationService} from "./annotation.service";
import { VariantAnnotationModalComponent } from './variant-annotation-modal/variant-annotation-modal.component';
import { VariantAgGridValidationComponent } from './variant-ag-grid-validation/variant-ag-grid-validation.component';
import {ReportService} from "./report.service";
import {ConditionRenderer, LoaderRenderer, ReportsComponent} from './reports/reports.component';
import { ReportResultComponent } from './report-result/report-result.component';
import {VariantAgGridCheckboxFilterComponent} from "./variant-ag-grid/variant-ag-grid-checkbox-filter.component";
import { ReportViewerComponent } from './report-viewer/report-viewer.component';

@NgModule({
  declarations: [
    AppComponent,
    VcfTableComponent,
    TableFilter,
    VariantFilterQueryComponent,
    SelectFieldFilterPipe,
    VariantTableConfigComponent,
    VariantDetailComponent,
    IgvComponent,
    FrequencyChartComponent,
    ImpactChartComponent,
    LoginComponent,
    LolliplotComponent,
    ExistingQueriesComponent,
    VariantQueryContainerComponent,
    BedTableComponent,
    QueryModalComponent,
    QueryResultComponent,
    PresetModalComponent,
    QueryBuilderComponent,
    QueryPresetsComponent,
    GenePanelPresetsComponent,
    EmptyComponent,
    UserComponent,
    UserChangePasswordComponent,
    EmailDirective,
    CompareDirective,
    GroupAdminComponent,
    BedGenomeModalComponent,
    GroupUserManagementComponent,
    GroupUserAddComponent,
    GroupVcfManagementComponent,
    GroupBedManagementComponent,
    VariantAgGridComponent,
    CoverageAgGridComponent,
    VariantAgGridPathogenicityComponent,
    ConditionSelectComponent,
    VariantAnnotationModalComponent,
    VariantAgGridCheckboxFilterComponent,
    VariantAgGridTrioComponent,
    VariantAgGridValidationComponent,
    ReportsComponent,
    ConditionRenderer,
    LoaderRenderer,
    ReportResultComponent,
    ReportViewerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    PopoverModule.forRoot(),
    CollapseModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    TabsModule.forRoot(),
    BsDropdownModule.forRoot(),
    ButtonsModule.forRoot(),
    AppRoutingModule,
    ChartsModule,
    AgGridModule.withComponents([])
  ],
  providers: [
    AnnotationService,
    VcfService,
    VariantService,
    AuthenticationService,
    ExternalProvidersService,
    QueryService,
    BedService,
    AggregationsService,
    AuthGuard,
    HomeGuard,
    QueriesGuard,
    AdminGuard,
    FileService,
    PresetService,
    UserService,
    GroupAdminService,
    ReportService],
  bootstrap: [AppComponent],
  entryComponents: [
    VariantTableConfigComponent,
    QueryModalComponent,
    PresetModalComponent,
    QueryPresetsComponent,
    GenePanelPresetsComponent,
    UserComponent,
    UserChangePasswordComponent,
    VariantAgGridPathogenicityComponent,
    VariantAnnotationModalComponent,
    GroupUserAddComponent,
    VariantAgGridTrioComponent,
    VariantAgGridCheckboxFilterComponent,
    VariantAgGridValidationComponent,
    ConditionRenderer,
    LoaderRenderer,
    BedGenomeModalComponent
  ]
})
export class AppModule {}
