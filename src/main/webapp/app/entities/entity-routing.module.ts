import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'author',
        data: { pageTitle: 'testprojectApp.author.home.title' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'testprojectApp.book.home.title' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'car',
        data: { pageTitle: 'testprojectApp.car.home.title' },
        loadChildren: () => import('./car/car.module').then(m => m.CarModule),
      },
      {
        path: 'dog',
        data: { pageTitle: 'testprojectApp.dog.home.title' },
        loadChildren: () => import('./dog/dog.module').then(m => m.DogModule),
      },
      {
        path: 'echo',
        data: { pageTitle: 'testprojectApp.echo.home.title' },
        loadChildren: () => import('./echo/echo.module').then(m => m.EchoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
