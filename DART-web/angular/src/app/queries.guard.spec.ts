import { TestBed, async, inject } from '@angular/core/testing';

import { QueriesGuard } from './queries.guard';

describe('HomeGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [QueriesGuard]
    });
  });

  it('should ...', inject([QueriesGuard], (guard: QueriesGuard) => {
    expect(guard).toBeTruthy();
  }));
});
