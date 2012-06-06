/*
 * Copyright (c) 2010 - 2012 Novus Partners, Inc. <http://novus.com>
 *
 * Module:        salat-util
 * Class:         TypeMatchers.scala
 * Last modified: 2012-04-28 20:34:21 EDT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project:      http://github.com/novus/salat
 * Wiki:         http://github.com/novus/salat/wiki
 * Mailing list: http://groups.google.com/group/scala-salat
 */
package com.novus.salat

import scala.math.{ BigDecimal => SBigDecimal }
import scala.tools.scalap.scalax.rules.scalasig.{ TypeRefType, Type, Symbol }

protected[salat] object Types {
  val Date = "java.util.Date"
  val DateTime = Set("org.joda.time.DateTime", "org.scala_tools.time.TypeImports.DateTime")
  val Oid = "org.bson.types.ObjectId"
  val SBigDecimal = classOf[SBigDecimal].getName
  val Option = "scala.Option"
  val Map = ".Map"
  val Traversables = Set(".Seq", ".List", ".Vector", ".Set", ".Buffer", ".ArrayBuffer", ".IndexedSeq", ".LinkedList", ".DoubleLinkedList")

  def isOption(sym: Symbol) = sym.path == Option

  def isMap(symbol: Symbol) = symbol.path.endsWith(Map)

  def isTraversable(symbol: Symbol) = Traversables.exists(symbol.path.endsWith(_))

  def isBigDecimal(symbol: Symbol) = symbol.path == SBigDecimal
}

protected[salat] object TypeMatchers {

  def matchesOneType(t: Type, name: String): Option[Type] = t match {
    case TypeRefType(_, symbol, List(arg)) if symbol.path == name => Some(arg)
    case _ => None
  }

  def matches(t: TypeRefType, name: String) = t.symbol.path == name

  def matches(t: TypeRefType, names: Traversable[String]) = names.exists(t.symbol.path == _)

  def matchesMap(t: Type) = t match {
    case TypeRefType(_, symbol, k :: v :: Nil) if Types.isMap(symbol) => Some(k -> v)
    case _ => None
  }

  def matchesTraversable(t: Type) = t match {
    case TypeRefType(_, symbol, List(arg)) if Types.isTraversable(symbol) => Some(arg)
    case _ => None
  }
}

protected[salat] object IsOption {
  def unapply(t: Type): Option[Type] = TypeMatchers.matchesOneType(t, Types.Option)
}

protected[salat] object IsMap {
  def unapply(t: Type): Option[(Type, Type)] = TypeMatchers.matchesMap(t)
}

protected[salat] object IsTraversable {
  def unapply(t: Type): Option[Type] = TypeMatchers.matchesTraversable(t)
}

protected[salat] object IsScalaBigDecimal {
  def unapply(t: Type): Option[Type] = TypeMatchers.matchesOneType(t, Types.SBigDecimal)
}