package de.twenty11.unitprofile.domain;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.twenty11.unitprofile.domain.Invocation;


public class InvocationTest {

    @Test
    public void should_render_simple_invocation_tree() {
        Invocation root = new Invocation("object", "method");
        String treetable = root.treetable();
        System.out.println(treetable);
        assertThat(treetable, containsString("<td>object#method</td>"));
    }
    
    @Test
    public void should_render_nested_invocation_tree() {
        Invocation root = new Invocation("object", "method");
        new Invocation(root, "object", "sub",1);
        String treetable = root.treetable();
        System.out.println(treetable);
        assertThat(treetable, containsString("<td>object#method</td>"));
        assertThat(treetable, containsString("<td>object#sub</td>"));
    }
}
