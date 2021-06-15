jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BookService } from '../service/book.service';

import { BookComponent } from './book.component';

describe('Component Tests', () => {
  describe('Book Management Component', () => {
    let comp: BookComponent;
    let fixture: ComponentFixture<BookComponent>;
    let service: BookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BookComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(BookComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BookComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BookService);

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
      expect(comp.books?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
