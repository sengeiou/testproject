jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DogService } from '../service/dog.service';

import { DogComponent } from './dog.component';

describe('Component Tests', () => {
  describe('Dog Management Component', () => {
    let comp: DogComponent;
    let fixture: ComponentFixture<DogComponent>;
    let service: DogService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DogComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(DogComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(DogService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.dogs?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
