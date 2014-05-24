package org.tindalos.principle.app.service

object MyApp extends App {

  val x = Some("Bela")

  val li = List(1, 2)


  println(li.map(_ * 2))
  //x.flatMap(x => x.+x)

  println("Hello Zsolti")

  println(List(Some(1), None, Some(2)).flatten)


  case class Company( name :String, region :String, avgSalary :Int )

  case class Employee( name :String, companyName :String, age :Int )

  val companies = List( Company( "SAL", "HE", 2000 ),
    Company( "GOK", "DA", 2500 ),
    Company( "MIK", "DA", 3000 ) )

  val employees = List( Employee( "Joana", "GOK", 20 ),
    Employee( "Mikey", "MIK", 31 ),
    Employee( "Susan", "MIK", 27 ),
    Employee( "Frank", "GOK", 28 ),
    Employee( "Ellen", "SAL", 29 ) )

  val result =
    for( e <- employees
         if e.age > 25;
         salary = e.age * 100;

         c <- companies
         if c.region == "DA";
         if c.name == e.companyName;
         if c.avgSalary < salary
    )
    yield ( e.name, c.name, salary - c.avgSalary )

  println( result )   // List(  (Mikey, MIK, 100),  (Frank, GOK, 300)  )
}