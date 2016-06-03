package org.libss.logic.squeryl.mysql

import org.squeryl.dsl._
import java.util.Date
import org.squeryl.dsl.ast.FunctionNode

/**
  * date: 03.06.2016 00:03
  * author: Kaa
  *
  * My sql functions squeryl helper
  */

trait MySQLFunctions {
  def formatDate(d: TypedExpression[Date, TDate],
                 f: TypedExpression[String, TString])
                (implicit fac: TypedExpressionFactory[String, TString]) = fac.convert(new FunctionNode("DATE_FORMAT", Seq(d, f)))

  def random()(implicit fac: TypedExpressionFactory[Double, TDouble]) = fac.convert(new FunctionNode("RAND", Nil))

  def substr(f: TypedExpression[String, TString],
             from: TypedExpression[Int, TInt],
             to: TypedExpression[Int, TInt])
            (implicit fac: TypedExpressionFactory[String, TString]) = fac.convert(new FunctionNode("SUBSTR", Seq(f, from, to)))

  def concat(f: TypedExpression[String, TString] *)
            (implicit fac: TypedExpressionFactory[String, TString]) = fac.convert(new FunctionNode("CONCAT", f.toSeq))

  def maximum(f: TypedExpression[Long, TLong] *)
             (implicit fac: TypedExpressionFactory[Long, TLong]) = fac.convert(new FunctionNode("MAX", f.toSeq))

  def minimum(f: TypedExpression[Long, TLong] *)
             (implicit fac: TypedExpressionFactory[Long, TLong]) = fac.convert(new FunctionNode("MIN", f.toSeq))

  def ifNull( f: TypedExpression[String, TString],
              defaultExpr: TypedExpression[String, TString])
            (implicit fac: TypedExpressionFactory[String, TString]) = fac.convert(new FunctionNode("IFNULL", Seq(f, defaultExpr)))
}
