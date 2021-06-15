import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EchoDetailComponent } from './echo-detail.component';

describe('Component Tests', () => {
  describe('Echo Management Detail Component', () => {
    let comp: EchoDetailComponent;
    let fixture: ComponentFixture<EchoDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [EchoDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ echo: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(EchoDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(EchoDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load echo on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.echo).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
