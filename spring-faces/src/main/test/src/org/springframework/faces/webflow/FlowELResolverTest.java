package src.org.springframework.faces.webflow;

import junit.framework.TestCase;
import org.apache.el.ExpressionFactoryImpl;
import org.apache.myfaces.test.el.MockELContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.support.FluentParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.expression.el.WebFlowELExpressionParser;
import org.springframework.webflow.test.MockRequestContext;

import javax.el.ELContext;
import java.io.Serializable;

public class FlowELResolverTest extends TestCase {
    private final org.springframework.faces.webflow.FlowELResolver resolver = new org.springframework.faces.webflow.FlowELResolver();
    private WebFlowELExpressionParser parser = new WebFlowELExpressionParser(new ExpressionFactoryImpl());

    private final RequestContext requestContext = new MockRequestContext();

    private final ELContext elContext = new MockELContext();

    @Before
    public void setUp() {
        RequestContextHolder.setRequestContext(this.requestContext);
    }

    @After
    public void tearDown() {
        RequestContextHolder.setRequestContext(null);
    }

    @Test
    public void testRequestContextResolve() {
        Object actual = this.resolver.getValue(this.elContext, null, "flowRequestContext");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertNotNull(actual);
        Assert.assertSame(this.requestContext, actual);
    }

    @Test
    public void testImplicitFlowResolve() {
        Object actual = this.resolver.getValue(this.elContext, null, "flowScope");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertNotNull(actual);
        Assert.assertSame(this.requestContext.getFlowScope(), actual);
    }

    @Test
    public void testFlowResourceResolve() {
        ApplicationContext applicationContext = new StaticWebApplicationContext();
        ((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
        Object actual = this.resolver.getValue(this.elContext, null, "resourceBundle");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertNotNull(actual);
        Assert.assertSame(applicationContext, actual);
    }

    @Test
    public void testScopeResolve() {
        this.requestContext.getFlowScope().put("test", "test");
        Object actual = this.resolver.getValue(this.elContext, null, "test");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertEquals("test", actual);
    }

    @Test
    public void testMapAdaptableResolve() {
        LocalAttributeMap base = new LocalAttributeMap();
        base.put("test", "test");
        Object actual = this.resolver.getValue(this.elContext, base, "test");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertEquals("test", actual);
    }

    @Test
    public void testBeanResolveWithRequestContext() {
        StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
        ((Flow) this.requestContext.getActiveFlow()).setApplicationContext(applicationContext);
        applicationContext.registerSingleton("test", Bean.class);
        Object actual = this.resolver.getValue(this.elContext, null, "test");
        Assert.assertTrue(this.elContext.isPropertyResolved());
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual instanceof Bean);
    }

    @Test
    public void testBeanResolveWithoutRequestContext() {
        RequestContextHolder.setRequestContext(null);
        Object actual = this.resolver.getValue(this.elContext, null, "test");
        Assert.assertFalse(this.elContext.isPropertyResolved());
        Assert.assertNull(actual);
    }

    public void testResolveSpringBean() {
        MockRequestContext context = new MockRequestContext();
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.getBeanFactory().registerSingleton("testBean", new TestBean());
        context.getRootFlow().setApplicationContext(ac);
        Expression exp = parser.parseExpression("testBean", new FluentParserContext().evaluate(RequestContext.class));
        assertSame(ac.getBean("testBean"), exp.getValue(context));
    }

    public static class Bean {
    }

    class TestBean implements Serializable {

        private int amount = 0;

        public boolean equals(Object o) {
            if (!(o instanceof TestBean)) {
                return false;
            }
            return amount == ((TestBean) o).amount;
        }

        public int hashCode() {
            return amount * 29;
        }

        public String toString() {
            return "[TestBean amount = " + amount + "]";
        }
    }
}
