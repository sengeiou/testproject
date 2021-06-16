jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AuthorService } from '../service/author.service';

import { AuthorComponent } from './author.component';

describe('Component Tests', () => {
  describe('Author Management Component', () => {
    let comp: AuthorComponent;
    let fixture: ComponentFixture<AuthorComponent>;
    let service: AuthorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AuthorComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(AuthorComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AuthorComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(AuthorService);

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
      expect(comp.authors?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
