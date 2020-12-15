import { TestBed, inject } from '@angular/core/testing';

import { VcfService } from './vcf.service';

describe('VcfService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [VcfService]
    });
  });

  it('should be created', inject([VcfService], (service: VcfService) => {
    expect(service).toBeTruthy();
  }));
});
