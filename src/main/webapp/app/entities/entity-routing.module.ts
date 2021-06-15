import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'actor',
        data: { pageTitle: 'testProjectApp.actor.home.title' },
        loadChildren: () => import('./actor/actor.module').then(m => m.ActorModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'testProjectApp.book.home.title' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'card',
        data: { pageTitle: 'testProjectApp.card.home.title' },
        loadChildren: () => import('./card/card.module').then(m => m.CardModule),
      },
      {
        path: 'dog',
        data: { pageTitle: 'testProjectApp.dog.home.title' },
        loadChildren: () => import('./dog/dog.module').then(m => m.DogModule),
      },
      {
        path: 'echo',
        data: { pageTitle: 'testProjectApp.echo.home.title' },
        loadChildren: () => import('./echo/echo.module').then(m => m.EchoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
