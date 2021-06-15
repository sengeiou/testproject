import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DogDetailComponent } from './dog-detail.component';

describe('Component Tests', () => {
  describe('Dog Management Detail Component', () => {
    let comp: DogDetailComponent;
    let fixture: ComponentFixture<DogDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [DogDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ dog: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(DogDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DogDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load dog on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.dog).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
