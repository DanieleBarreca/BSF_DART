import { TestBed, inject } from '@angular/core/testing';

import { ExternalProvidersService } from './external-providers.service';

describe('ExternalProvidersService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ExternalProvidersService]
    });
  });

  it('should be created', inject([ExternalProvidersService], (service: ExternalProvidersService) => {
    expect(service).toBeTruthy();
  }));
});
