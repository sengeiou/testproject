jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CardService } from '../service/card.service';

import { CardComponent } from './card.component';

describe('Component Tests', () => {
  describe('Card Management Component', () => {
    let comp: CardComponent;
    let fixture: ComponentFixture<CardComponent>;
    let service: CardService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CardComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(CardComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CardComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CardService);

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
      expect(comp.cards?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
