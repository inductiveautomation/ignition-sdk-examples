# Project Resource - Designer

In the Designer scope, you will register your resource workspace and a project browser node.
It is generally recommended to subclass `TabbedResourceWorkspace` to give end users the most familiar editing paradigm.
`TabbedResourceWorkspace` will automatically create instances of your `ResourceEditor` subclass and manage bookkeeping
for you.