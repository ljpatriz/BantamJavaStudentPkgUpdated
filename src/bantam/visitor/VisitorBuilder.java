package bantam.visitor;

import bantam.ast.*;
import java.util.function.Function;

public class VisitorBuilder {

	private Function<BinaryArithPlusExpr, Object> visitBinaryArithPlusExpr;
	private Function<BinaryArithExpr, Object> visitBinaryArithExpr;
	private Function<BinaryCompGeqExpr, Object> visitBinaryCompGeqExpr;
	private Function<BinaryCompGtExpr, Object> visitBinaryCompGtExpr;
	private Function<BinaryArithModulusExpr, Object> visitBinaryArithModulusExpr;
	private Function<BinaryArithDivideExpr, Object> visitBinaryArithDivideExpr;
	private Function<BinaryArithTimesExpr, Object> visitBinaryArithTimesExpr;
	private Function<BinaryArithMinusExpr, Object> visitBinaryArithMinusExpr;
	private Function<BinaryCompEqExpr, Object> visitBinaryCompEqExpr;
	private Function<BinaryCompExpr, Object> visitBinaryCompExpr;
	private Function<BinaryExpr, Object> visitBinaryExpr;
	private Function<ArrayAssignExpr, Object> visitArrayAssignExpr;
	private Function<BinaryCompNeExpr, Object> visitBinaryCompNeExpr;
	private Function<BinaryCompLtExpr, Object> visitBinaryCompLtExpr;
	private Function<BinaryCompLeqExpr, Object> visitBinaryCompLeqExpr;
	private Function<ConstExpr, Object> visitConstExpr;
	private Function<ArrayExpr, Object> visitArrayExpr;
	private Function<VarExpr, Object> visitVarExpr;
	private Function<UnaryDecrExpr, Object> visitUnaryDecrExpr;
	private Function<ConstIntExpr, Object> visitConstIntExpr;
	private Function<ConstBooleanExpr, Object> visitConstBooleanExpr;
	private Function<ConstStringExpr, Object> visitConstStringExpr;
	private Function<UnaryExpr, Object> visitUnaryExpr;
	private Function<BinaryLogicOrExpr, Object> visitBinaryLogicOrExpr;
	private Function<BinaryLogicAndExpr, Object> visitBinaryLogicAndExpr;
	private Function<BinaryLogicExpr, Object> visitBinaryLogicExpr;
	private Function<UnaryNegExpr, Object> visitUnaryNegExpr;
	private Function<UnaryNotExpr, Object> visitUnaryNotExpr;
	private Function<UnaryIncrExpr, Object> visitUnaryIncrExpr;
	private Function<Formal, Object> visitFormal;
	private Function<FormalList, Object> visitFormalList;
	private Function<Method, Object> visitMethod;
	private Function<Field, Object> visitField;
	private Function<ExprStmt, Object> visitExprStmt;
	private Function<DeclStmt, Object> visitDeclStmt;
	private Function<Stmt, Object> visitStmt;
	private Function<StmtList, Object> visitStmtList;
	private Function<ClassList, Object> visitClassList;
	private Function<Program, Object> visitProgram;
	private Function<ListNode, Object> visitListNode;
	private Function<ASTNode, Object> visitASTNode;
	private Function<Class_, Object> visitClass_;
	private Function<MemberList, Object> visitMemberList;
	private Function<Member, Object> visitMember;
	private Function<NewArrayExpr, Object> visitNewArrayExpr;
	private Function<NewExpr, Object> visitNewExpr;
	private Function<DispatchExpr, Object> visitDispatchExpr;
	private Function<Expr, Object> visitExpr;
	private Function<InstanceofExpr, Object> visitInstanceofExpr;
	private Function<CastExpr, Object> visitCastExpr;
	private Function<AssignExpr, Object> visitAssignExpr;
	private Function<BreakStmt, Object> visitBreakStmt;
	private Function<ForStmt, Object> visitForStmt;
	private Function<WhileStmt, Object> visitWhileStmt;
	private Function<IfStmt, Object> visitIfStmt;
	private Function<BlockStmt, Object> visitBlockStmt;
	private Function<ReturnStmt, Object> visitReturnStmt;
	private Function<ExprList, Object> visitExprList;


	public Visitor build() {
		return new Visitor() {
			@Override
			public Object visit(BinaryArithPlusExpr node) {
				return visitBinaryArithPlusExpr.apply(node);
			}

			@Override
			public Object visit(BinaryArithExpr node) {
				return visitBinaryArithExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompGeqExpr node) {
				return visitBinaryCompGeqExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompGtExpr node) {
				return visitBinaryCompGtExpr.apply(node);
			}

			@Override
			public Object visit(BinaryArithModulusExpr node) {
				return visitBinaryArithModulusExpr.apply(node);
			}

			@Override
			public Object visit(BinaryArithDivideExpr node) {
				return visitBinaryArithDivideExpr.apply(node);
			}

			@Override
			public Object visit(BinaryArithTimesExpr node) {
				return visitBinaryArithTimesExpr.apply(node);
			}

			@Override
			public Object visit(BinaryArithMinusExpr node) {
				return visitBinaryArithMinusExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompEqExpr node) {
				return visitBinaryCompEqExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompExpr node) {
				return visitBinaryCompExpr.apply(node);
			}

			@Override
			public Object visit(BinaryExpr node) {
				return visitBinaryExpr.apply(node);
			}

			@Override
			public Object visit(ArrayAssignExpr node) {
				return visitArrayAssignExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompNeExpr node) {
				return visitBinaryCompNeExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompLtExpr node) {
				return visitBinaryCompLtExpr.apply(node);
			}

			@Override
			public Object visit(BinaryCompLeqExpr node) {
				return visitBinaryCompLeqExpr.apply(node);
			}

			@Override
			public Object visit(ConstExpr node) {
				return visitConstExpr.apply(node);
			}

			@Override
			public Object visit(ArrayExpr node) {
				return visitArrayExpr.apply(node);
			}

			@Override
			public Object visit(VarExpr node) {
				return visitVarExpr.apply(node);
			}

			@Override
			public Object visit(UnaryDecrExpr node) {
				return visitUnaryDecrExpr.apply(node);
			}

			@Override
			public Object visit(ConstIntExpr node) {
				return visitConstIntExpr.apply(node);
			}

			@Override
			public Object visit(ConstBooleanExpr node) {
				return visitConstBooleanExpr.apply(node);
			}

			@Override
			public Object visit(ConstStringExpr node) {
				return visitConstStringExpr.apply(node);
			}

			@Override
			public Object visit(UnaryExpr node) {
				return visitUnaryExpr.apply(node);
			}

			@Override
			public Object visit(BinaryLogicOrExpr node) {
				return visitBinaryLogicOrExpr.apply(node);
			}

			@Override
			public Object visit(BinaryLogicAndExpr node) {
				return visitBinaryLogicAndExpr.apply(node);
			}

			@Override
			public Object visit(BinaryLogicExpr node) {
				return visitBinaryLogicExpr.apply(node);
			}

			@Override
			public Object visit(UnaryNegExpr node) {
				return visitUnaryNegExpr.apply(node);
			}

			@Override
			public Object visit(UnaryNotExpr node) {
				return visitUnaryNotExpr.apply(node);
			}

			@Override
			public Object visit(UnaryIncrExpr node) {
				return visitUnaryIncrExpr.apply(node);
			}

			@Override
			public Object visit(Formal node) {
				return visitFormal.apply(node);
			}

			@Override
			public Object visit(FormalList node) {
				return visitFormalList.apply(node);
			}

			@Override
			public Object visit(Method node) {
				return visitMethod.apply(node);
			}

			@Override
			public Object visit(Field node) {
				return visitField.apply(node);
			}

			@Override
			public Object visit(ExprStmt node) {
				return visitExprStmt.apply(node);
			}

			@Override
			public Object visit(DeclStmt node) {
				return visitDeclStmt.apply(node);
			}

			@Override
			public Object visit(Stmt node) {
				return visitStmt.apply(node);
			}

			@Override
			public Object visit(StmtList node) {
				return visitStmtList.apply(node);
			}

			@Override
			public Object visit(ClassList node) {
				return visitClassList.apply(node);
			}

			@Override
			public Object visit(Program node) {
				return visitProgram.apply(node);
			}

			@Override
			public Object visit(ListNode node) {
				return visitListNode.apply(node);
			}

			@Override
			public Object visit(ASTNode node) {
				return visitASTNode.apply(node);
			}

			@Override
			public Object visit(Class_ node) {
				return visitClass_.apply(node);
			}

			@Override
			public Object visit(MemberList node) {
				return visitMemberList.apply(node);
			}

			@Override
			public Object visit(Member node) {
				return visitMember.apply(node);
			}

			@Override
			public Object visit(NewArrayExpr node) {
				return visitNewArrayExpr.apply(node);
			}

			@Override
			public Object visit(NewExpr node) {
				return visitNewExpr.apply(node);
			}

			@Override
			public Object visit(DispatchExpr node) {
				return visitDispatchExpr.apply(node);
			}

			@Override
			public Object visit(Expr node) {
				return visitExpr.apply(node);
			}

			@Override
			public Object visit(InstanceofExpr node) {
				return visitInstanceofExpr.apply(node);
			}

			@Override
			public Object visit(CastExpr node) {
				return visitCastExpr.apply(node);
			}

			@Override
			public Object visit(AssignExpr node) {
				return visitAssignExpr.apply(node);
			}

			@Override
			public Object visit(BreakStmt node) {
				return visitBreakStmt.apply(node);
			}

			@Override
			public Object visit(ForStmt node) {
				return visitForStmt.apply(node);
			}

			@Override
			public Object visit(WhileStmt node) {
				return visitWhileStmt.apply(node);
			}

			@Override
			public Object visit(IfStmt node) {
				return visitIfStmt.apply(node);
			}

			@Override
			public Object visit(BlockStmt node) {
				return visitBlockStmt.apply(node);
			}

			@Override
			public Object visit(ReturnStmt node) {
				return visitReturnStmt.apply(node);
			}

			@Override
			public Object visit(ExprList node) {
				return visitExprList.apply(node);
			}

	};
}

	public void setVisitBinaryArithPlusExpr(Function<BinaryArithPlusExpr, Object> visitFunction) {
		this.visitBinaryArithPlusExpr = visitFunction;
	}

	public void setVisitBinaryArithExpr(Function<BinaryArithExpr, Object> visitFunction) {
		this.visitBinaryArithExpr = visitFunction;
	}

	public void setVisitBinaryCompGeqExpr(Function<BinaryCompGeqExpr, Object> visitFunction) {
		this.visitBinaryCompGeqExpr = visitFunction;
	}

	public void setVisitBinaryCompGtExpr(Function<BinaryCompGtExpr, Object> visitFunction) {
		this.visitBinaryCompGtExpr = visitFunction;
	}

	public void setVisitBinaryArithModulusExpr(Function<BinaryArithModulusExpr, Object> visitFunction) {
		this.visitBinaryArithModulusExpr = visitFunction;
	}

	public void setVisitBinaryArithDivideExpr(Function<BinaryArithDivideExpr, Object> visitFunction) {
		this.visitBinaryArithDivideExpr = visitFunction;
	}

	public void setVisitBinaryArithTimesExpr(Function<BinaryArithTimesExpr, Object> visitFunction) {
		this.visitBinaryArithTimesExpr = visitFunction;
	}

	public void setVisitBinaryArithMinusExpr(Function<BinaryArithMinusExpr, Object> visitFunction) {
		this.visitBinaryArithMinusExpr = visitFunction;
	}

	public void setVisitBinaryCompEqExpr(Function<BinaryCompEqExpr, Object> visitFunction) {
		this.visitBinaryCompEqExpr = visitFunction;
	}

	public void setVisitBinaryCompExpr(Function<BinaryCompExpr, Object> visitFunction) {
		this.visitBinaryCompExpr = visitFunction;
	}

	public void setVisitBinaryExpr(Function<BinaryExpr, Object> visitFunction) {
		this.visitBinaryExpr = visitFunction;
	}

	public void setVisitArrayAssignExpr(Function<ArrayAssignExpr, Object> visitFunction) {
		this.visitArrayAssignExpr = visitFunction;
	}

	public void setVisitBinaryCompNeExpr(Function<BinaryCompNeExpr, Object> visitFunction) {
		this.visitBinaryCompNeExpr = visitFunction;
	}

	public void setVisitBinaryCompLtExpr(Function<BinaryCompLtExpr, Object> visitFunction) {
		this.visitBinaryCompLtExpr = visitFunction;
	}

	public void setVisitBinaryCompLeqExpr(Function<BinaryCompLeqExpr, Object> visitFunction) {
		this.visitBinaryCompLeqExpr = visitFunction;
	}

	public void setVisitConstExpr(Function<ConstExpr, Object> visitFunction) {
		this.visitConstExpr = visitFunction;
	}

	public void setVisitArrayExpr(Function<ArrayExpr, Object> visitFunction) {
		this.visitArrayExpr = visitFunction;
	}

	public void setVisitVarExpr(Function<VarExpr, Object> visitFunction) {
		this.visitVarExpr = visitFunction;
	}

	public void setVisitUnaryDecrExpr(Function<UnaryDecrExpr, Object> visitFunction) {
		this.visitUnaryDecrExpr = visitFunction;
	}

	public void setVisitConstIntExpr(Function<ConstIntExpr, Object> visitFunction) {
		this.visitConstIntExpr = visitFunction;
	}

	public void setVisitConstBooleanExpr(Function<ConstBooleanExpr, Object> visitFunction) {
		this.visitConstBooleanExpr = visitFunction;
	}

	public void setVisitConstStringExpr(Function<ConstStringExpr, Object> visitFunction) {
		this.visitConstStringExpr = visitFunction;
	}

	public void setVisitUnaryExpr(Function<UnaryExpr, Object> visitFunction) {
		this.visitUnaryExpr = visitFunction;
	}

	public void setVisitBinaryLogicOrExpr(Function<BinaryLogicOrExpr, Object> visitFunction) {
		this.visitBinaryLogicOrExpr = visitFunction;
	}

	public void setVisitBinaryLogicAndExpr(Function<BinaryLogicAndExpr, Object> visitFunction) {
		this.visitBinaryLogicAndExpr = visitFunction;
	}

	public void setVisitBinaryLogicExpr(Function<BinaryLogicExpr, Object> visitFunction) {
		this.visitBinaryLogicExpr = visitFunction;
	}

	public void setVisitUnaryNegExpr(Function<UnaryNegExpr, Object> visitFunction) {
		this.visitUnaryNegExpr = visitFunction;
	}

	public void setVisitUnaryNotExpr(Function<UnaryNotExpr, Object> visitFunction) {
		this.visitUnaryNotExpr = visitFunction;
	}

	public void setVisitUnaryIncrExpr(Function<UnaryIncrExpr, Object> visitFunction) {
		this.visitUnaryIncrExpr = visitFunction;
	}

	public void setVisitFormal(Function<Formal, Object> visitFunction) {
		this.visitFormal = visitFunction;
	}

	public void setVisitFormalList(Function<FormalList, Object> visitFunction) {
		this.visitFormalList = visitFunction;
	}

	public void setVisitMethod(Function<Method, Object> visitFunction) {
		this.visitMethod = visitFunction;
	}

	public void setVisitField(Function<Field, Object> visitFunction) {
		this.visitField = visitFunction;
	}

	public void setVisitExprStmt(Function<ExprStmt, Object> visitFunction) {
		this.visitExprStmt = visitFunction;
	}

	public void setVisitDeclStmt(Function<DeclStmt, Object> visitFunction) {
		this.visitDeclStmt = visitFunction;
	}

	public void setVisitStmt(Function<Stmt, Object> visitFunction) {
		this.visitStmt = visitFunction;
	}

	public void setVisitStmtList(Function<StmtList, Object> visitFunction) {
		this.visitStmtList = visitFunction;
	}

	public void setVisitClassList(Function<ClassList, Object> visitFunction) {
		this.visitClassList = visitFunction;
	}

	public void setVisitProgram(Function<Program, Object> visitFunction) {
		this.visitProgram = visitFunction;
	}

	public void setVisitListNode(Function<ListNode, Object> visitFunction) {
		this.visitListNode = visitFunction;
	}

	public void setVisitASTNode(Function<ASTNode, Object> visitFunction) {
		this.visitASTNode = visitFunction;
	}

	public void setVisitClass_(Function<Class_, Object> visitFunction) {
		this.visitClass_ = visitFunction;
	}

	public void setVisitMemberList(Function<MemberList, Object> visitFunction) {
		this.visitMemberList = visitFunction;
	}

	public void setVisitMember(Function<Member, Object> visitFunction) {
		this.visitMember = visitFunction;
	}

	public void setVisitNewArrayExpr(Function<NewArrayExpr, Object> visitFunction) {
		this.visitNewArrayExpr = visitFunction;
	}

	public void setVisitNewExpr(Function<NewExpr, Object> visitFunction) {
		this.visitNewExpr = visitFunction;
	}

	public void setVisitDispatchExpr(Function<DispatchExpr, Object> visitFunction) {
		this.visitDispatchExpr = visitFunction;
	}

	public void setVisitExpr(Function<Expr, Object> visitFunction) {
		this.visitExpr = visitFunction;
	}

	public void setVisitInstanceofExpr(Function<InstanceofExpr, Object> visitFunction) {
		this.visitInstanceofExpr = visitFunction;
	}

	public void setVisitCastExpr(Function<CastExpr, Object> visitFunction) {
		this.visitCastExpr = visitFunction;
	}

	public void setVisitAssignExpr(Function<AssignExpr, Object> visitFunction) {
		this.visitAssignExpr = visitFunction;
	}

	public void setVisitBreakStmt(Function<BreakStmt, Object> visitFunction) {
		this.visitBreakStmt = visitFunction;
	}

	public void setVisitForStmt(Function<ForStmt, Object> visitFunction) {
		this.visitForStmt = visitFunction;
	}

	public void setVisitWhileStmt(Function<WhileStmt, Object> visitFunction) {
		this.visitWhileStmt = visitFunction;
	}

	public void setVisitIfStmt(Function<IfStmt, Object> visitFunction) {
		this.visitIfStmt = visitFunction;
	}

	public void setVisitBlockStmt(Function<BlockStmt, Object> visitFunction) {
		this.visitBlockStmt = visitFunction;
	}

	public void setVisitReturnStmt(Function<ReturnStmt, Object> visitFunction) {
		this.visitReturnStmt = visitFunction;
	}

	public void setVisitExprList(Function<ExprList, Object> visitFunction) {
		this.visitExprList = visitFunction;
	}

}