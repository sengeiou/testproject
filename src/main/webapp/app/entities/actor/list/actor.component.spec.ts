jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ActorService } from '../service/actor.service';

import { ActorComponent } from './actor.component';

describe('Component Tests', () => {
  describe('Actor Management Component', () => {
    let comp: ActorComponent;
    let fixture: ComponentFixture<ActorComponent>;
    let service: ActorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ActorComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ActorComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ActorComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ActorService);

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
      expect(comp.actors?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
