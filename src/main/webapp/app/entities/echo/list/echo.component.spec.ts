jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EchoService } from '../service/echo.service';

import { EchoComponent } from './echo.component';

describe('Component Tests', () => {
  describe('Echo Management Component', () => {
    let comp: EchoComponent;
    let fixture: ComponentFixture<EchoComponent>;
    let service: EchoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [EchoComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(EchoComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(EchoComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(EchoService);

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
      expect(comp.echoes?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
