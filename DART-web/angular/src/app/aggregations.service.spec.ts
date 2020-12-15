import { TestBed, inject } from '@angular/core/testing';

import { AggregationsService } from './aggregations.service';

describe('EmblUniprotService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AggregationsService]
    });
  });

  it('should be created', inject([AggregationsService], (service: AggregationsService) => {
    expect(service).toBeTruthy();
  }));
});
