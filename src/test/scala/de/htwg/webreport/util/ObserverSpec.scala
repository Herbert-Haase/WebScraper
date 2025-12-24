package de.htwg.webreport.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.util.{Observable, Observer}

class TestObservable extends Observable {
  def getSubscribers: Vector[Observer] = subscribers
}

class TestObserver_o extends Observer {
  var wasNotified = false
  var lastFilterFlag: Boolean = false
  override def update(isFilterUpdate: Boolean): Unit = {
    wasNotified = true
    lastFilterFlag = isFilterUpdate
  }
}

class ObserverSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "add an observer" in {
      val observable = new TestObservable()
      val observer = new TestObserver_o()
      observable.getSubscribers should be(empty)
      observable.add(observer)
      observable.getSubscribers should contain(observer)
    }

    "remove an observer" in {
      val observable = new TestObservable()
      val observer1 = new TestObserver_o()
      val observer2 = new TestObserver_o()
      observable.add(observer1)
      observable.add(observer2)
      observable.getSubscribers should contain allOf (observer1, observer2)

      observable.remove(observer1)
      observable.getSubscribers should not contain (observer1)
      observable.getSubscribers should contain(observer2)
    }

    "notify observers with correct flags" in {
      val observable = new TestObservable()
      val observer1 = new TestObserver_o()
      val observer2 = new TestObserver_o()
      observable.add(observer1)
      observable.add(observer2)

      observer1.wasNotified should be(false)
      observer2.wasNotified should be(false)

      observable.notifyObservers()
      observer1.wasNotified should be(true)
      observer2.wasNotified should be(true)
      observer1.lastFilterFlag should be(false)
      observer2.lastFilterFlag should be(false)

      observer1.wasNotified = false
      observer2.wasNotified = false

      observable.notifyObservers(isFilterUpdate = true)
      observer1.wasNotified should be(true)
      observer2.wasNotified should be(true)
      observer1.lastFilterFlag should be(true)
      observer2.lastFilterFlag should be(true)
    }

    "not fail when notifying with no observers" in {
      val observable = new TestObservable()
      noException should be thrownBy {
        observable.notifyObservers()
      }
    }
  }
  "An Observer" should {

    "receive update calls with and without parameter" in {
      class TestObserver extends Observer {
        var calls: List[Boolean] = Nil
        override def update(isFilterUpdate: Boolean): Unit =
          calls = calls :+ isFilterUpdate
      }

      val observer = new TestObserver

      observer.update()
      observer.update(true)

      observer.calls shouldBe List(false, true)
    }
  }
}